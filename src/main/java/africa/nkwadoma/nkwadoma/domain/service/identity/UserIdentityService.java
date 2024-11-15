package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.*;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase  {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityMapper userIdentityMapper;
    private final BlackListedTokenAdapter blackListedTokenAdapter;


    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException {
        UserIdentityValidator.validateUserIdentity(userIdentity);
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(userIdentity.getCreatedBy().trim());
//        validateEmailDomain(userIdentity.getEmail().trim(), foundEmployee.getMeedlUser().getEmail().trim());
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        userIdentityOutputPort.save(userIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setMeedlUser(userIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);

        sendEmail.sendColleagueEmail(userIdentity);

        return userIdentity;
    }
    @Override
    public AccessTokenResponse login(UserIdentity userIdentity)throws MeedlException {
        UserIdentityValidator.validateDataElement(userIdentity.getEmail());
        UserIdentityValidator.validateDataElement(userIdentity.getPassword());
        return identityManagerOutPutPort.login(userIdentity);
    }

    @Override
    public void logout(UserIdentity userIdentity) throws MeedlException {
        identityManagerOutPutPort.logout(userIdentity);
        blackListedTokenAdapter.blackListToken(createBlackList(userIdentity.getAccessToken()));
    }
    private BlackListedToken createBlackList(String accessToken){
        BlackListedToken blackListedToken = new BlackListedToken();
        blackListedToken.setAccess_token(accessToken);
        return blackListedToken;
    }
    @Scheduled(cron = "0 0 8,20 * * *") // Runs at 8 AM and 8 PM every day
    public void clearBlackListedToken() throws MeedlException {
        if(!blackListedTokenAdapter.findAll().isEmpty()) {
            for (BlackListedToken blackListedToken : blackListedTokenAdapter.findAll()) {
                if (isExpired(blackListedToken.getAccess_token())) {
                    blackListedTokenAdapter.deleteToken(blackListedToken);
                }
            }
            log.info("cron is running....");
        }
    }

    private boolean isExpired(String accessToken) throws MeedlException {
        try {
            JWT jwt = JWTParser.parse(accessToken);
            Date expirationDate = jwt.getJWTClaimsSet().getExpirationTime();
            return Objects.requireNonNull(expirationDate).toInstant().isBefore(Instant.now());
        } catch (ParseException e) {
            throw new MeedlException("Parse error...  : "+ e.getMessage());
        }
    }
    @Override
    public UserIdentity createPassword(String token, String password) throws MeedlException {
        UserIdentity userIdentity = getUserIdentityFromToken(password, token);
        userIdentity = identityManagerOutPutPort.createPassword(userIdentity.getEmail(), password);
        return userIdentity;
    }

    @Override
    public void resetPassword(String token, String password) throws MeedlException {
        UserIdentity userIdentity = getUserIdentityFromToken(password, token);
        userIdentity.setNewPassword(password);
        identityManagerOutPutPort.resetPassword(userIdentity);
    }

    private UserIdentity getUserIdentityFromToken(String password, String token) throws MeedlException {
        MeedlValidator.validatePassword(password);
        validateDataElement(token);
        String email = tokenUtils.decodeJWTGetEmail(token);
        log.info("User email from token {}", email);
        return userIdentityOutputPort.findByEmail(email);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validatePassword(userIdentity.getNewPassword());
        login(userIdentity);
        if(checkNewPasswordMatchLastFive(userIdentity)){
            throw new IdentityException(PASSWORD_NOT_ACCEPTED.getMessage());
        }
        userIdentity.setPassword(userIdentity.getNewPassword());
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        identityManagerOutPutPort.setPassword(userIdentity);
    }

    @Override
    public void forgotPassword(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        try {
            UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
            identityManagerOutPutPort.getUserByEmail(foundUser.getEmail());
            sendOrganizationEmployeeEmailUseCase.sendEmail(foundUser);
        } catch (MeedlException e) {
            log.error("Error : either user doesn't exist on our platform or email sending was not successful. {}'", e.getMessage());
        }
    }
    @Override
    public UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(userIdentity.getId());
        validateDataElement(userIdentity.getReactivationReason());
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        userIdentity = identityManagerOutPutPort.enableUserAccount(foundUserIdentity);
        log.info("User reactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(userIdentity.getId());
        validateDataElement(userIdentity.getDeactivationReason());
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        foundUserIdentity.setDeactivationReason(userIdentity.getDeactivationReason());
        userIdentity = identityManagerOutPutPort.disableUserAccount(foundUserIdentity);
        log.info("User deactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity){
        List<UserRepresentation> userRepresentations = identityManagerOutPutPort.getUserRepresentations(userIdentity);
        return false;
    }

}

