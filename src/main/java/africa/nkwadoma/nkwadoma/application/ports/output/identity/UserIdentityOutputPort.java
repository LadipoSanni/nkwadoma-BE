package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

public interface UserIdentityOutputPort {
    UserIdentity save(UserIdentity userIdentity) throws MeedlException;
    UserIdentity findById(String id) throws MeedlException;
    void deleteUserById(String id) throws MeedlException;
    UserIdentity findByEmail(String email) throws MeedlException;
    void deleteUserByEmail(String email) throws MeedlException;

    void verifyUser(String actorId);
}
