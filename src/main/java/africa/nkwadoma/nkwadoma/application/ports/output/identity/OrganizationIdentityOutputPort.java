package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.*;

public interface OrganizationIdentityOutputPort {
    OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException;
    OrganizationIdentity findByEmail(String email) throws MeedlException;
    void delete(String rcNumber) throws MeedlException;
    OrganizationIdentity findById(String id) throws MeedlException;

    boolean existsById(String organizationId);
}

