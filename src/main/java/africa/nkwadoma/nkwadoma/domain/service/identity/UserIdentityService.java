package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import com.nimbusds.jwt.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.security.crypto.password.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityMapper userIdentityMapper;
    private final BlackListedTokenAdapter blackListedTokenAdapter;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;


    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException {
        log.info("Inviting colleague");
        MeedlValidator.validateObjectInstance(userIdentity);
        userIdentity.validate();
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(userIdentity.getCreatedBy().trim());
        log.info("Found employee: {}", foundEmployee);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        UserIdentity savedUserIdentity = userIdentityOutputPort.save(userIdentity);
        log.info("Employee user identity saved to DB: {}", savedUserIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setStatus(ActivationStatus.INVITED);
        organizationEmployeeIdentity.setMeedlUser(userIdentity);
        OrganizationEmployeeIdentity savedEmployee = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        log.info("Saved organization employee identity: {}", savedEmployee);

        OrganizationIdentity organizationIdentity =
                organizationIdentityOutputPort.findById(foundEmployee.getOrganization());
        log.info("Found organization identity: {}", organizationIdentity);
        sendEmail.sendColleagueEmail(organizationIdentity.getName(),userIdentity);

        return userIdentity;
    }

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity)throws MeedlException {
        MeedlValidator.validateEmail(userIdentity.getEmail());
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
    public UserIdentity createPassword(String token, String password) throws MeedlException {
        log.info("request got into service layer {}",password);
//        passwordPreviouslyCreated(token);
        MeedlValidator.validateDataElement(token);
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
    public void resetPassword(String token, String password) throws MeedlException {
//        passwordPreviouslyCreated(token);
        UserIdentity userIdentity = getUserIdentityFromToken(password, token);
        userIdentity.setNewPassword(password);
        identityManagerOutPutPort.resetPassword(userIdentity);
//        blackListedTokenAdapter.blackListToken(createBlackList(token));
    }

    private UserIdentity getUserIdentityFromToken(String password, String token) throws MeedlException {
        MeedlValidator.validatePassword(password);
        MeedlValidator.validateDataElement(token);
        String email = tokenUtils.decodeJWTGetEmail(token);
        log.info("User email from token {}", email);
        return userIdentityOutputPort.findByEmail(email);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
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
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
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
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getReactivationReason(), "Reason for reactivation is required.");
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        userIdentity = identityManagerOutPutPort.enableUserAccount(foundUserIdentity);
        log.info("User reactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getDeactivationReason(), "Reason for deactivation required");
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        foundUserIdentity.setDeactivationReason(userIdentity.getDeactivationReason());
        userIdentity = identityManagerOutPutPort.disableUserAccount(foundUserIdentity);
        log.info("User deactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity) {
        List<UserRepresentation> userRepresentations = identityManagerOutPutPort.getUserRepresentations(userIdentity);
        return false;
    }

    @Override
    public UserIdentity viewUserDetail(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateUUID(userIdentity.getId());
        log.info("Viewing user details");
        return userIdentityOutputPort.findById(userIdentity.getId());
    }

}

