package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.UserUseCase;
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
                    Set.of(IdentityRole.PORTFOLIO_MANAGER, IdentityRole.MEEDL_ASSOCIATE)
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
        if ((userIdentity.isEnablePhoneNumberMFA() || userIdentity.isEnableEmailMFA())
            && userIdentity.isDisableMFA()){
            log.error("MFA cannot be disabled and enabled at the same time as one or more factors are selected for a disabled mfa {}", userIdentity);
            throw new MeedlException("MFA cannot be disabled and enabled at the same time");
        }
        UserIdentity foundUser = userIdentityOutputPort.findById(userIdentity.getId());
        if(userIdentity.isDisableMFA()){
            return disableMFA(foundUser);
        }
        if (userIdentity.isEnableEmailMFA()){
            return enableEmailMFA(foundUser);
        }

        if (userIdentity.isEnablePhoneNumberMFA()){
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
                (!IdentityRole.isOrganizationStaff(foundUser.getRole()))
//                        ||
//                        (!IdentityRole.i)))
        ){
          log.error("You are not authorized to update image. User with id {} and role {}", userIdentity.getId(), foundUser.getRole());
          throw new MeedlException("You are not authorized to update image");
        }
        foundUser.setImage(userIdentity.getImage());
        userIdentityOutputPort.save(foundUser);
        log.info("Image uploaded success.");
    }

    private String enableEmailMFA(UserIdentity foundUser) throws MeedlException {
        log.info("Email mfa is selected. User Email {}", foundUser.getEmail());
        applyMFASettings(foundUser, true, Boolean.TRUE, Boolean.FALSE);
        return "Email MFA enabled successfully";
    }

    private String disableMFA(UserIdentity foundUser) throws MeedlException {
        log.warn("MFA is being disabled for user with email {}", foundUser.getEmail());
        applyMFASettings(foundUser, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
        return "MFA disabled";
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

        applyMFASettings(foundUser, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
        return "Phone number MFA enabled successfully";
    }

    private void applyMFASettings(UserIdentity foundUser, boolean mfaEnabled, boolean emailMFA, boolean phoneMFA) throws MeedlException {
        foundUser.setMFAEnabled(mfaEnabled);
        foundUser.setEnableEmailMFA(emailMFA);
        foundUser.setEnablePhoneNumberMFA(phoneMFA);
        log.info("Saving new mfa settings mfaEnabled {} , emailMFA {}, phoneMFA {}, found user {}", mfaEnabled, emailMFA, phoneMFA, foundUser);
        userIdentityOutputPort.save(foundUser);
    }

    private static void validatePhoneNumber(UserIdentity foundUser) throws MeedlException {
        String elevenDigitNumber = MeedlValidator.formatPhoneNumber(foundUser.getMFAPhoneNumber());
        MeedlValidator.validateElevenDigits(elevenDigitNumber, "Please provide a valid phone number for MFA");
    }

}

