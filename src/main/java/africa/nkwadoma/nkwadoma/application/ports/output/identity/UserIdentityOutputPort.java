package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

import java.util.List;

public interface UserIdentityOutputPort {
    UserIdentity save(UserIdentity userIdentity) throws MeedlException;

    UserIdentity findById(String id) throws MeedlException;

    void deleteUserById(String id) throws MeedlException;

    UserIdentity findByEmail(String email) throws MeedlException;

    void deleteUserByEmail(String email) throws MeedlException;
    UserIdentity findByBvn(String bvn) throws MeedlException;
    List<UserIdentity> findAllByRole(IdentityRole identityRole) throws MeedlException;

    List<UserIdentity> findAllByRoles(List<IdentityRole> roles) throws MeedlException;
}
