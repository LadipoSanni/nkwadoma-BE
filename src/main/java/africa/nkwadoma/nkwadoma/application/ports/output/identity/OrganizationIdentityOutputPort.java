package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

public interface OrganizationIdentityOutputPort {
    OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MiddlException;
    OrganizationIdentity findByEmail(String email) throws MiddlException;
    void delete(String rcNumber) throws MiddlException;
    OrganizationIdentity findById(String id) throws MiddlException;

    boolean existsById(String organizationId);
}

