package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;

public interface IdentityManagementOutputPort {
    UserIdentity createUser(UserIdentity userIdentity);
}
