package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailTokenOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import com.nimbusds.jwt.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.*;
import org.springframework.scheduling.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    private final AesOutputPort tokenUtils;
    private final EmailTokenOutputPort emailTokenManager;
    private final BlackListedTokenAdapter blackListedTokenAdapter;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity)throws MeedlException {
        MeedlValidator.validateDataElement(userIdentity.getPassword(), "Password must be provided to login");
        MeedlValidator.validateEmail(userIdentity.getEmail());
        String password = tokenUtils.decryptAES(userIdentity.getPassword(), "Password provided is not valid. Contact admin.");
        userIdentity.setPassword(password);
        MeedlValidator.validatePassword(userIdentity.getPassword());
        return identityManagerOutPutPort.login(userIdentity);
    }

    @Override
    public AccessTokenResponse refreshToken(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getRefreshToken(), UserMessages.REFRESH_TOKEN_CANNOT_BE_EMPTY.getMessage());
        return identityManagerOutPutPort.refreshToken(userIdentity);
    }

    @Override
    public void logout(UserIdentity userIdentity) throws MeedlException {
        identityManagerOutPutPort.logout(userIdentity);
        blackListedTokenAdapter.blackListToken(createBlackList(userIdentity.getAccessToken()));
    }
    private BlackListedToken createBlackList(String accessToken) throws MeedlException {
        BlackListedToken blackListedToken = new BlackListedToken();
        blackListedToken.setAccess_token(accessToken);
        blackListedToken.setExpirationDate(getExpirationDate(accessToken));
        return blackListedToken;
    }

    @Scheduled(cron = "0 0 8,20 * * *") // Runs at 8 AM and 8 PM every day
    public void clearBlackListedToken() {
        log.info("cron job deleting expired blacklisted tokens...");
        List<BlackListedToken> expiredTokens = blackListedTokenAdapter.findExpiredTokens();
        expiredTokens.forEach(blackListedTokenAdapter::deleteToken);
    }

    private LocalDateTime getExpirationDate(String token) throws MeedlException {
        try {
            JWT jwt = JWTParser.parse(token);
            Date expirationDate = jwt.getJWTClaimsSet().getExpirationTime();
            return expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            throw new MeedlException("Error extracting date and time from token ...  : "+ e.getMessage());
        }
    }
    private void passwordPreviouslyCreated(String token) throws IdentityException {
        log.info("checking if its previously created  {}",token);
        if (blackListedTokenAdapter.isPresent(token)){
            log.info("Password already created before. Method called more than once with the same token.");
            throw new IdentityException("Password already created. Try login or forgot password. Or contact the admin ");
        }
    }

    @Override
    public UserIdentity createPassword(String token, String encryptedPassword) throws MeedlException {
        log.info("request got into service layer {}",encryptedPassword);
//        passwordPreviouslyCreated(token);
        String password = validatePassword(encryptedPassword, token);
        passwordPreviouslyCreated(token);
        UserIdentity foundUser = getUserIdentityFromToken(password, token);
        UserIdentity userIdentity = identityManagerOutPutPort.createPassword(
                UserIdentity.builder().email(foundUser.getEmail()).password(password).build());
        userIdentity.setRole(foundUser.getRole());
        log.info("User Identity after password has been created: {}", foundUser);
//        blackListedTokenAdapter.blackListToken(createBlackList(token));
//        log.info("done getting user identity frm token {}",userIdentity);
//        userIdentity = identityManagerOutPutPort.createPassword(UserIdentity.builder().email(userIdentity.getEmail()).password(password).build());
//        blackListedTokenAdapter.blackListToken(createBlackList(token));
        return userIdentity;
    }

    @Override
    public void resetPassword(String token, String encryptedPassword) throws MeedlException {
//        passwordPreviouslyCreated(token);
        String password = validatePassword(encryptedPassword, token);
        UserIdentity userIdentity = getUserIdentityFromToken(password, token);
        userIdentity.setNewPassword(password);
        identityManagerOutPutPort.resetPassword(userIdentity);
//        blackListedTokenAdapter.blackListToken(createBlackList(token));
    }

    private String validatePassword(String encryptedPassword, String token) throws MeedlException {
        MeedlValidator.validateDataElement(encryptedPassword,  "Password cannot be empty");
        MeedlValidator.validateDataElement(token, "Invalid token provided.");
        String password = tokenUtils.decryptAES(encryptedPassword, "Password provided is not valid. Contact admin.");
        MeedlValidator.validatePassword(password);
        return password;
    }

    private UserIdentity getUserIdentityFromToken(String password, String token) throws MeedlException {
        String email = emailTokenManager.decodeJWTGetEmail(token);
        log.info("User email from token {}", email);
        return userIdentityOutputPort.findByEmail(email);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validatePassword(userIdentity.getNewPassword());
        login(userIdentity);
        if (userIdentity.getNewPassword().equals(userIdentity.getPassword())){
            log.warn("{}", UserMessages.NEW_PASSWORD_AND_CURRENT_PASSWORD_CANNOT_BE_SAME.getMessage());
            throw new IdentityException(UserMessages.NEW_PASSWORD_AND_CURRENT_PASSWORD_CANNOT_BE_SAME.getMessage());
        }
        if(checkNewPasswordMatchLastFive(userIdentity)){
            throw new IdentityException(PASSWORD_NOT_ACCEPTED.getMessage());
        }
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now());
        identityManagerOutPutPort.setPassword(userIdentity);
        log.info("Password changed successfully for user with id: {}",userIdentity.getId());
    }

    @Override
    public void forgotPassword(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        try {
            UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
            identityManagerOutPutPort.getUserByEmail(foundUser.getEmail());
            sendOrganizationEmployeeEmailUseCase.sendForgotPasswordEmail(foundUser);
        } catch (MeedlException e) {
            log.error("Error : either user doesn't exist on our platform or email sending was not successful. {}'", e.getMessage());
        }
    }

    @Override
    public UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getReactivationReason(), "Reason for reactivation is required.");
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        userIdentity = identityManagerOutPutPort.enableUserAccount(foundUserIdentity);
        log.info("User reactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getDeactivationReason(), "Reason for deactivation required");
        log.info("About to find deactivating actor by id {}", userIdentity.getId());
        UserIdentity foundUserToDeactivate = userIdentityOutputPort.findById(userIdentity.getId());
        checkIfUserAllowedToDeactivateAccount(foundUserToDeactivate, userIdentity);
        foundUserToDeactivate.setDeactivationReason("User deactivated by : "+ userIdentity.getCreatedBy() + ". Reason : "+userIdentity.getDeactivationReason());
        userIdentity = identityManagerOutPutPort.disableUserAccount(foundUserToDeactivate);
        log.info("User on key cloak deactivated successfully.");
        deactivateEmployee(userIdentity);
        log.info("User deactivated successfully {}", userIdentity.getId());
        asynchronousMailingOutputPort.notifyDeactivatedUser(userIdentity);
        return userIdentity;
    }

    private void deactivateEmployee(UserIdentity userIdentity) throws MeedlException {
        Optional<OrganizationEmployeeIdentity> optionalOrganizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId());
        if (optionalOrganizationEmployeeIdentity.isPresent()) {
            log.info("User being deactivated is an employee. User id {}", userIdentity.getId());
            OrganizationEmployeeIdentity organizationEmployeeIdentity = optionalOrganizationEmployeeIdentity.get();
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.DEACTIVATED);
            organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
            log.info("Employee deactivated successfully! Employee id {} , organization id {}", organizationEmployeeIdentity.getId(), organizationEmployeeIdentity.getOrganization());
        }
    }

    public void checkIfUserAllowedToDeactivateAccount(UserIdentity userToDeactivate, UserIdentity userIdentity) throws MeedlException {
        UserIdentity foundActor = userIdentityOutputPort.findById(userIdentity.getCreatedBy());
        log.info("Is actor with email {} and role {} , allowed to deactivate user with email {} and role {}", foundActor.getEmail(), foundActor.getRole(), userToDeactivate.getEmail(), userToDeactivate.getRole());
        if (foundActor.getId().equals(userToDeactivate.getId())){
            log.error("User attempts to deactivate self found actor {}, user to deactivate {}", foundActor, userToDeactivate);
            throw new MeedlException("You are not allowed to deactivate yourself.");
        }
        checkIfOrganizationAdminCanDeactivateAccount(userToDeactivate, foundActor);
        checkIfPortfolioManagerCanDeactivateAccount(userToDeactivate, foundActor);
        if (IdentityRole.MEEDL_SUPER_ADMIN.equals(userToDeactivate.getRole())){
            log.info("An attempt was made to deactivate Meedl's supper admin {} \n ----------------------------> attempt to deactivate Meedls super admin was made by ------------------------->{}", userToDeactivate, foundActor);
            asynchronousNotificationOutputPort.notifySuperAdminOfDeactivationAttempt(foundActor);
            throw new MeedlException("You are not allowed to deactivate the super admin on Meedl");
        }
        log.info("Done with validation, actor is allowed to deactivate user.");
    }

    private void checkIfPortfolioManagerCanDeactivateAccount(UserIdentity userToDeactivate, UserIdentity foundActor) throws MeedlException {
        if (IdentityRole.PORTFOLIO_MANAGER.equals(foundActor.getRole())) {
            checkDeactivationIsAuthorised(
                    foundActor,
                    userToDeactivate,
                    Set.of(IdentityRole.PORTFOLIO_MANAGER, IdentityRole.MEEDL_ASSOCIATE)
            );
        }
    }

    private void checkIfOrganizationAdminCanDeactivateAccount(UserIdentity userToDeactivate, UserIdentity foundActor) throws MeedlException {
        if (IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(foundActor.getRole()) ||
                IdentityRole.ORGANIZATION_ADMIN.equals(foundActor.getRole())) {
            Optional <OrganizationEmployeeIdentity> deactivatingEmployee = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userToDeactivate.getId());
                    if (deactivatingEmployee.isEmpty()) {
                        log.error("User can not perform deactivation as user is not an employee on the platform");
                        throw new MeedlException("You cannot deactivate this user, please contact Meedl admin!");
                    };
            Optional <OrganizationEmployeeIdentity> employeeToDeactivate = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userToDeactivate.getId());
            if (employeeToDeactivate.isEmpty()) {
                log.error("User to be deactivated is not an employee, there and organization cannot deactivate this user as user is meant to be an employee in the same organization as the actor.");
                throw new MeedlException("You can only deactivate employees in your organizations");
            };

            OrganizationIdentity actorOrganization = organizationIdentityOutputPort.findByEmail(employeeToDeactivate.get().getOrganization());
            OrganizationIdentity userToDeactivateOrganization = organizationIdentityOutputPort.findByEmail(employeeToDeactivate.get().getOrganization());
            if (!actorOrganization.getId().equals(userToDeactivateOrganization.getId())){
                log.error("Attempting to deactivate a user in {} \nWhere as actor is in {}", userToDeactivateOrganization, actorOrganization);
                throw new MeedlException("You are not allowed to deactivate a user that is not in your organization");
            }
            checkDeactivationIsAuthorised(
                    foundActor,
                    userToDeactivate,
                    Set.of(IdentityRole.ORGANIZATION_ADMIN, IdentityRole.ORGANIZATION_ASSOCIATE)
            );
        }
    }


    private void checkDeactivationIsAuthorised(UserIdentity foundActor , UserIdentity userToDeactivate, Set<IdentityRole> allowedTargetRoles) throws MeedlException {
        if (!allowedTargetRoles.contains(userToDeactivate.getRole())) {
            log.error("You are not authorized to deactivate user with role {} for user with email {}. The actor email is {} and the role of the actor is {}",
                    userToDeactivate.getRole(), userToDeactivate.getEmail(), foundActor.getEmail(), foundActor.getRole());
            throw new MeedlException("You are not authorized to deactivate this user");
        }
    }

    @Override
    public boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity) {
        List<UserRepresentation> userRepresentations = identityManagerOutPutPort.getUserRepresentations(userIdentity);
        return false;
    }

    @Override
    public UserIdentity viewUserDetail(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        log.info("Viewing user details");
        UserIdentity foundUser = userIdentityOutputPort.findById(userIdentity.getId());
        if (foundUser != null && StringUtils.isNotEmpty(foundUser.getAlternateEmail())){
            log.info("Additional details has been added.");
            foundUser.setAdditionalDetailsCompleted(Boolean.TRUE);
            log.info("Alternate email {}", foundUser.getAlternateEmail());
        }
        return foundUser;
    }

}

