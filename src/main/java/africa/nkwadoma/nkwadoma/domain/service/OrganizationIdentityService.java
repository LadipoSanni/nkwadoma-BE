package africa.nkwadoma.nkwadoma.domain.service;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationIdentityService implements CreateOrganizationUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;


    @Override
    public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MiddlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        UserIdentityValidator.validateUserIdentity(organizationIdentity.getOrganizationEmployees());
        organizationIdentity = identityManagerOutPutPort.createOrganization(organizationIdentity);
        UserIdentity newUser = identityManagerOutPutPort.createUser(organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser());
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        employeeIdentity.setMiddlUser(newUser);
        employeeIdentity.setOrganization(organizationIdentity.getId());

        //save entities to DB
        organizationIdentityOutputPort.save(organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        userIdentityOutputPort.save(organizationEmployeeIdentity.getMiddlUser());
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);


        //send invite email to organization admin
       return null;
    }



}
