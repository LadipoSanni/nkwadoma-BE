package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.UserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailTokenOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.MFAType;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
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
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Component
public class UserIdentityService implements UserUseCase {
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
    private final FinancierOutputPort financierOutputPort;

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

        activateCooperateFinancier(userIdentity);

//        blackListedTokenAdapter.blackListToken(createBlackList(token));
//        log.info("done getting user identity frm token {}",userIdentity);
//        userIdentity = identityManagerOutPutPort.createPassword(UserIdentity.builder().email(userIdentity.getEmail()).password(password).build());
//        blackListedTokenAdapter.blackListToken(createBlackList(token));
        return userIdentity;
    }

    private void activateCooperateFinancier(UserIdentity userIdentity) throws MeedlException {
        if (userIdentity.getRole().isCooperateStaff()){
            OrganizationEmployeeIdentity organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId())
                            .orElseThrow(() -> new MeedlException("Organization employee identity not found"));
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
            organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
            activateCooperation(userIdentity, organizationEmployeeIdentity);
        }
    }

    private void activateCooperation(UserIdentity userIdentity, OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        if (userIdentity.getRole().equals(IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN)){
            OrganizationIdentity organizationIdentity =
                    organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
            organizationIdentity.setActivationStatus(ActivationStatus.ACTIVE);
            organizationIdentity.setOrganizationType(OrganizationType.COOPERATE);
            organizationIdentityOutputPort.save(organizationIdentity);

            Financier financier = financierOutputPort.findByIdentity(organizationIdentity.getId());
            financier.setActivationStatus(ActivationStatus.ACTIVE);
            financierOutputPort.save(financier);
        }
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
        MeedlValidator.validatePassword(tokenUtils.decryptAES(userIdentity.getPassword(), "Invalid password for current password"));
        MeedlValidator.validatePassword(tokenUtils.decryptAES(userIdentity.getNewPassword(), "Invalid new password provided"));
        try {
            login(userIdentity);
        }catch (MeedlException e){
            log.info("Password invalid on change password {} user email {}", e.getMessage(), userIdentity.getEmail());
            throw new MeedlException("Password incorrect");
        }
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
        userIdentity.setNewPassword(tokenUtils.decryptAES(userIdentity.getNewPassword(), "Provide valid password to update"));
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
        UserIdentity userToActivate = userIdentityOutputPort.findById(userIdentity.getId());

        checkIfUserAllowedForAccountActivationActivity(userIdentity, userIdentity, ActivationStatus.ACTIVE);
        userIdentity.setReactivationReason("User activated by : "+ userIdentity.getCreatedBy() + ". Reason : "+userIdentity.getDeactivationReason());

        userIdentity = identityManagerOutPutPort.enableUserAccount(userToActivate);
        log.info("User on key cloak reactivation successfully.");
        performEmployeeActivation(userIdentity, ActivationStatus.ACTIVE);
        log.info("Employee reactivated successfully. User id: {}", userIdentity.getId());

        asynchronousMailingOutputPort.notifyUserOnActivationActivityOnUserAccount(userIdentity, ActivationStatus.ACTIVE);
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getDeactivationReason(), "Reason for deactivation required");
        log.info("About to find user to deactivate by id {}", userIdentity.getId());
        UserIdentity foundUserToDeactivate = userIdentityOutputPort.findById(userIdentity.getId());

        checkIfUserAllowedForAccountActivationActivity(foundUserToDeactivate, userIdentity, ActivationStatus.DEACTIVATED);
        foundUserToDeactivate.setDeactivationReason("User deactivated by : "+ userIdentity.getCreatedBy() + ". Reason : "+userIdentity.getDeactivationReason());

        userIdentity = identityManagerOutPutPort.disableUserAccount(foundUserToDeactivate);
        log.info("User on key cloak deactivated successfully.");
        performEmployeeActivation(userIdentity, ActivationStatus.DEACTIVATED);
        log.info("Employee deactivated successfully. User id: {}", userIdentity.getId());

        asynchronousMailingOutputPort.notifyUserOnActivationActivityOnUserAccount(userIdentity, ActivationStatus.DEACTIVATED);
        return userIdentity;
    }

    private void performEmployeeActivation(UserIdentity userIdentity, ActivationStatus activationStatus) throws MeedlException {
        Optional<OrganizationEmployeeIdentity> optionalOrganizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId());
        if (optionalOrganizationEmployeeIdentity.isPresent()) {
            log.info("User being {} is an employee. User id {}", activationStatus, userIdentity.getId());
            OrganizationEmployeeIdentity organizationEmployeeIdentity = optionalOrganizationEmployeeIdentity.get();
            organizationEmployeeIdentity.setActivationStatus(activationStatus);
            organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
            log.info("Employee {} successfully! Employee id {} , organization id {}", activationStatus, organizationEmployeeIdentity.getId(), organizationEmployeeIdentity.getOrganization());
        }
    }

    public void checkIfUserAllowedForAccountActivationActivity(UserIdentity userActedUpon, UserIdentity userIdentity, ActivationStatus activationStatus) throws MeedlException {
        UserIdentity foundActor = userIdentityOutputPort.findById(userIdentity.getCreatedBy());
        log.info("Is actor with email {} and role {} , allowed to {} user with email {} and role {}", foundActor.getEmail(), foundActor.getRole(), activationStatus, userActedUpon.getEmail(), userActedUpon.getRole());
        if (foundActor.getId().equals(userActedUpon.getId())){
            log.error("User attempts to {} self found actor {}, user is {}", activationStatus, foundActor, userActedUpon);
            throw new MeedlException("You are not allowed to "+activationStatus+" yourself.");
        }
        checkIfOrganizationAdminCanPerformAccountActivationActivity(userActedUpon, foundActor, activationStatus);
        checkIfPortfolioManagerCanPerformAccountActivationActivity(userActedUpon, foundActor, activationStatus);
        if (IdentityRole.MEEDL_SUPER_ADMIN.equals(userActedUpon.getRole())){
            log.info("An attempt was made to {} Meedl's supper admin {} \n ----------------------------> attempt on Meedls super admin was made by ------------------------->{}",activationStatus, userActedUpon, foundActor);
            asynchronousNotificationOutputPort.notifySuperAdminOfActivationActivityAttempt(foundActor);
            throw new MeedlException("You are not allowed to "+activationStatus+" the super admin on Meedl");
        }
        log.info("Done with validation, actor is allowed to {} user.", activationStatus);
    }

    private void checkIfPortfolioManagerCanPerformAccountActivationActivity(UserIdentity userToDeactivate, UserIdentity foundActor, ActivationStatus activationStatus) throws MeedlException {
        if (IdentityRole.PORTFOLIO_MANAGER.equals(foundActor.getRole())) {
            log.info("Checking to see if portfolio manager can perform activation activity");
            checkDeactivationIsAuthorised(
                    activationStatus,
                    foundActor,
                    userToDeactivate,
                    Set.of(IdentityRole.PORTFOLIO_MANAGER, IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE)
            );
        }
    }

    private void checkIfOrganizationAdminCanPerformAccountActivationActivity(UserIdentity userToDeactivate, UserIdentity foundActor, ActivationStatus activationStatus) throws MeedlException {
        if (IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(foundActor.getRole()) ||
                IdentityRole.ORGANIZATION_ADMIN.equals(foundActor.getRole())) {
            Optional <OrganizationEmployeeIdentity> deactivatingEmployee = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userToDeactivate.getId());
                    if (deactivatingEmployee.isEmpty()) {
                        log.error("User can not perform {} as user is not an employee on the platform", activationStatus);
                        throw new MeedlException("You cannot "+activationStatus+" this user, please contact Meedl admin!");
                    };
            Optional <OrganizationEmployeeIdentity> employeeToDeactivate = organizationEmployeeIdentityOutputPort.findByMeedlUserId(userToDeactivate.getId());
            if (employeeToDeactivate.isEmpty()) {
                log.error("User to be {} is not an employee, there and organization cannot {} this user as user is meant to be an employee in the same organization as the actor.", activationStatus, activationStatus);
                throw new MeedlException("You can only "+activationStatus+" employees in your organizations");
            };

            OrganizationIdentity actorOrganization = organizationIdentityOutputPort.findByEmail(employeeToDeactivate.get().getOrganization());
            OrganizationIdentity userToDeactivateOrganization = organizationIdentityOutputPort.findByEmail(employeeToDeactivate.get().getOrganization());
            if (!actorOrganization.getId().equals(userToDeactivateOrganization.getId())){
                log.error("Attempting to {} a user in {} \nWhere as actor is in {}", actorOrganization, userToDeactivateOrganization, actorOrganization);
                throw new MeedlException("You are not allowed to "+activationStatus+" a user that is not in your organization");
            }
            log.info("Checking if actor with role {} can perform activation activity {}", foundActor.getRole(), activationStatus);
            checkDeactivationIsAuthorised(
                    activationStatus,
                    foundActor,
                    userToDeactivate,
                    Set.of(IdentityRole.ORGANIZATION_ADMIN, IdentityRole.ORGANIZATION_ASSOCIATE)
            );
        }
    }


    private void checkDeactivationIsAuthorised(ActivationStatus activationStatus, UserIdentity foundActor , UserIdentity userToDeactivate, Set<IdentityRole> allowedTargetRoles) throws MeedlException {
        if (!allowedTargetRoles.contains(userToDeactivate.getRole())) {
            log.error("You are not authorized to {} user with role {} for user with email {}. The actor email is {} and the role of the actor is {}",
                    activationStatus, userToDeactivate.getRole(), userToDeactivate.getEmail(), foundActor.getEmail(), foundActor.getRole());
            throw new MeedlException("You are not authorized to "+activationStatus+" this user");
        }
        log.info("Actor has permission to perform activation activity");
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

    @Override
    public String manageMFA(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        UserIdentity foundUser = userIdentityOutputPort.findById(userIdentity.getId());
        if(MFAType.MFA_DISABLED.equals(userIdentity.getMfaType())){
            foundUser.setMfaType(userIdentity.getMfaType());
             userIdentityOutputPort.save(foundUser);
            return "MFA disabled";
        }
        if (MFAType.EMAIL_MFA.equals(userIdentity.getMfaType())){
            foundUser.setMfaType(userIdentity.getMfaType());
            userIdentityOutputPort.save(foundUser);
            return "Email MFA enabled successfully";
        }

        if (MFAType.PHONE_NUMBER_MFA.equals(userIdentity.getMfaType())){
            return enablePhoneNumberForMFA(userIdentity, foundUser);
        }
        log.warn("Unable to determine MFA option selected by user with email {}", foundUser.getEmail());
        return "Unable to determine MFA option selected";
    }

    @Override
    public void uploadImage(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getImage(), "Image not provided");

        UserIdentity foundUser = userIdentityOutputPort.findById(userIdentity.getId());
        if ((!IdentityRole.isMeedlStaff(foundUser.getRole())) &&
                (!IdentityRole.isOrganizationStaff(foundUser.getRole())) &&
                (!IdentityRole.FINANCIER.equals(foundUser.getRole())) &&
                        (!IdentityRole.isCooperateFinancier(foundUser.getRole()))) {
            log.error("You are not authorized to update image. User with id {} and role {}", userIdentity.getId(), foundUser.getRole());
            throw new MeedlException("You are not authorized to update image");
        }
        foundUser.setImage(userIdentity.getImage());
        userIdentityOutputPort.save(foundUser);
        log.info("Image uploaded success.");
    }
    @Override
    public UserIdentity assignRole(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(userIdentity.getCreatedBy(), UserMessages.INVALID_ROLE_ASSIGNER_ID.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_ROLE_ASSIGNEE_ID.getMessage());

        UserIdentity superAdmin = userIdentityOutputPort.findById(userIdentity.getCreatedBy());
        UserIdentity foundUserToAssign = userIdentityOutputPort.findById(userIdentity.getId());

        if (IdentityRole.MEEDL_SUPER_ADMIN.equals(superAdmin.getRole())){
            if (!IdentityRole.isAssignableMeedlRole(userIdentity.getRole())){
                log.error("User with id {} and role {} attempts to assign a non meedl role {} to user with id {}", superAdmin.getId(), superAdmin.getRole(), userIdentity.getRole(), userIdentity.getId());
                throw new MeedlException("You are not allowed to assign a non Meedl role an employee");
            }
        }else if (IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(superAdmin.getRole())){
            if (!IdentityRole.isAssignableOrganizationRole(userIdentity.getRole())){
                log.error("User with id {} and role {} attempts to assign a non organizational role {} to user with id {}", superAdmin.getId(), superAdmin.getRole(), userIdentity.getRole(), userIdentity.getId());
                throw new MeedlException("You are not allowed to assign a non Orgainzation role an employee");
            }
        }else {
            log.error("User with id {} and role {} attempts to assign role {} to user with id {}", superAdmin.getId(), superAdmin.getRole(), userIdentity.getRole(), userIdentity.getId());
            throw new MeedlException("You are not authorized to assign a role");
        }
        foundUserToAssign.setRole(userIdentity.getRole());
        return userIdentityOutputPort.save(foundUserToAssign);
    }

    private String enablePhoneNumberForMFA(UserIdentity userIdentity, UserIdentity foundUser) throws MeedlException {
        if (MeedlValidator.isNotEmptyString(userIdentity.getMFAPhoneNumber())){
            log.info("A new phone number is given for mfa {}", userIdentity.getMFAPhoneNumber());
            validatePhoneNumber(userIdentity);
            foundUser.setMFAPhoneNumber(userIdentity.getMFAPhoneNumber());
        }else if (MeedlValidator.isEmptyString(userIdentity.getMFAPhoneNumber())){
            log.info("No new phone number was provided for mfa. The old number is {}", foundUser.getMFAPhoneNumber());
            validatePhoneNumber(foundUser);
        }

        foundUser.setMfaType(userIdentity.getMfaType());
        userIdentityOutputPort.save(foundUser);
        return "Phone number MFA enabled successfully";
    }

    private static void validatePhoneNumber(UserIdentity foundUser) throws MeedlException {
        String elevenDigitNumber = MeedlValidator.formatPhoneNumber(foundUser.getMFAPhoneNumber());
        MeedlValidator.validateElevenDigits(elevenDigitNumber, "Please provide a valid phone number for MFA");
    }

}

