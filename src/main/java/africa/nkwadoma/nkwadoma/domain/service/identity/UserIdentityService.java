package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_HAS_BEEN_CREATED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_NOT_ACCEPTED;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.*;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityMapper userIdentityMapper;



    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException {
        UserIdentityValidator.validateUserIdentity(userIdentity);
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(userIdentity.getCreatedBy().trim());
        validateEmailDomain(userIdentity.getEmail().trim(), foundEmployee.getMiddlUser().getEmail().trim());
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        userIdentityOutputPort.save(userIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setMiddlUser(userIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);

        sendEmail.sendColleagueEmail(userIdentity);

        return userIdentity;
    }

    @Override
    public UserIdentity createPassword(String token, String password) throws MeedlException {
        validatePassword(password);
        validateDataElement(token);
        String email = tokenUtils.decodeJWT(token);
        log.info("The email of the user is: {} creating password", email);
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(email);
        log.info("The user found by the email is: {}", userIdentity);
        if (!userIdentity.isEmailVerified() && !userIdentity.isEnabled()) {
            userIdentity = identityManagerOutPutPort.createPassword(userIdentity.getEmail(), password);
            return userIdentity;
        }
        else throw new MeedlException(PASSWORD_HAS_BEEN_CREATED.getMessage());
    }



    @Override
    public AccessTokenResponse login(UserIdentity userIdentity)throws MeedlException {
        UserIdentityValidator.validateDataElement(userIdentity.getEmail());
        UserIdentityValidator.validateDataElement(userIdentity.getPassword());
        return identityManagerOutPutPort.login(userIdentity);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        validatePassword(userIdentity.getNewPassword());
        login(userIdentity);
        if(checkNewPasswordMatchLastFive(userIdentity)){
            throw new IdentityException(PASSWORD_NOT_ACCEPTED.getMessage());
        }
        userIdentity.setPassword(userIdentity.getNewPassword());
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        identityManagerOutPutPort.changePassword(userIdentity);
    }

    @Override
    public void resetPassword(String email, String password) throws MeedlException {
        UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
        validatePassword(password);
        identityManagerOutPutPort.createPassword(foundUser.getEmail(),password);
        foundUser.setPassword(password);
        userIdentityOutputPort.save(foundUser);
    }

    @Override
    public UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        validateDataElement(userIdentity.getId());
        validateDataElement(userIdentity.getReactivationReason());
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        foundUserIdentity.setReactivationReason(userIdentity.getReactivationReason());
        userIdentity = identityManagerOutPutPort.enableUserAccount(foundUserIdentity);
        userIdentityOutputPort.save(userIdentity);
        log.info("User reactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        validateDataElement(userIdentity.getId());
        validateDataElement(userIdentity.getDeactivationReason());
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        foundUserIdentity.setDeactivationReason(userIdentity.getDeactivationReason());
        userIdentity = identityManagerOutPutPort.disableUserAccount(foundUserIdentity);
        userIdentityOutputPort.save(userIdentity);
        log.info("User deactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }


    @Override
    public boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity){
        List<UserRepresentation> userRepresentations = identityManagerOutPutPort.getUserRepresentations(userIdentity);
        return false;
    }



}

