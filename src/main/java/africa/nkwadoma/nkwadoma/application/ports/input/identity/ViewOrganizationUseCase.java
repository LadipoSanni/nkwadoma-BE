package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.List;

public interface ViewOrganizationUseCase {
    List<OrganizationIdentity> search(String organizationName) throws MeedlException;

    OrganizationIdentity viewOrganizationDetails(String organizationId) throws MeedlException;
}
