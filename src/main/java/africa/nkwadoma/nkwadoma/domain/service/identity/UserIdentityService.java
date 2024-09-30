package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_HAS_BEEN_CREATED;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.validateEmailDomain;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.validatePassword;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final TokenGeneratorOutputPort tokenGeneratorOutputPort;




    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MiddlException {
        UserIdentity inviter = userIdentityOutputPort.findById(userIdentity.getCreatedBy());
        validateEmailDomain(userIdentity.getEmail(),inviter.getEmail());
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(inviter.getId());
        userIdentity.setRole(inviter.getRole());
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        userIdentityOutputPort.save(userIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setMiddlUser(userIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);

        return userIdentity;
    }

    @Override
    public void createPassword(String token, String password) throws MiddlException {
        validatePassword(password);
        String email = tokenGeneratorOutputPort.decodeJWT(token);
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(email);
        log.info("{}",userIdentity);

        if (!userIdentity.isEmailVerified()){
            userIdentity.setPassword(password);
            userIdentity.setEmailVerified(true);
            userIdentity.setEnabled(false);

            userIdentityOutputPort.save(userIdentity);
            identityManagerOutPutPort.createPassword(userIdentity.getEmail(), userIdentity.getPassword());
        }
       else throw new IdentityException(PASSWORD_HAS_BEEN_CREATED.getMessage());
    }


}
