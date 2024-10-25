package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MeedlException;

    void deactivateOrganization(String organizationId) throws MeedlException;

    void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MeedlException;

}
