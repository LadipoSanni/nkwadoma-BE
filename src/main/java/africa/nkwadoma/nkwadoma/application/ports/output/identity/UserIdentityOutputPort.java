package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;

public interface UserIdentityOutputPort {
    UserIdentity save(UserIdentity userIdentity) throws MiddlException;
    UserIdentity findById(String id) throws MiddlException;
    void deleteUserById(String id) throws MiddlException;
    UserIdentity findByEmail(String email) throws MiddlException;
    void deleteUserByEmail(String email) throws MiddlException;
    UserIdentity update(UserIdentity userIdentity) throws MiddlException;
}
