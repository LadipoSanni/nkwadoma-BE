package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;

public interface OrganizationIdentityOutputPort {
    OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MiddlException;
    OrganizationIdentity findByEmail(String email) throws MiddlException;
    void delete(String rcNumber) throws MiddlException;
    OrganizationIdentity findByRcNumber(String rcNumber) throws MiddlException;
    OrganizationIdentity update(OrganizationIdentity organizationIdentity) throws MiddlException;
}
