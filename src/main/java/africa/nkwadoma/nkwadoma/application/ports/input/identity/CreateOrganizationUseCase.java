package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.List;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MeedlException;

    void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MeedlException;

    List<OrganizationIdentity> search(String organizationName) throws MeedlException;
}
