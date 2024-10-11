package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MiddlException;

    void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MiddlException;

    OrganizationIdentity createOrganizationIdentityOnkeycloak(OrganizationIdentity organizationIdentity) throws MiddlException;

    OrganizationEmployeeIdentity saveOrganisationIdentityToDatabase(OrganizationIdentity organizationIdentity) throws MiddlException;
    // OrganizationIdentity inviteColleague(OrganizationIdentity organizationIdentity)throws MiddlException;

}
