package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MiddlException;

}
