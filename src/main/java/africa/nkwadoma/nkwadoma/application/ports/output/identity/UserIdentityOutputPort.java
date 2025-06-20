package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;

import java.util.List;

public interface UserIdentityOutputPort {
    UserIdentity save(UserIdentity userIdentity) throws IdentityException;

    UserIdentity findById(String id) throws InvestmentException;

    void deleteUserById(String id) throws MeedlException;

    UserIdentity findByEmail(String email) throws MeedlException;

    void deleteUserByEmail(String email) throws MeedlException;
    UserIdentity findByBvn(String bvn) throws MeedlException;
    List<UserIdentity> findAllByRole(IdentityRole identityRole) throws MeedlException;
}
