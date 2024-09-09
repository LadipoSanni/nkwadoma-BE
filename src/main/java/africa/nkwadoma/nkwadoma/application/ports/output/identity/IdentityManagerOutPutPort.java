package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;

public interface IdentityManagerOutPutPort {
    UserIdentity createUser(UserIdentity userIdentity) throws InfrastructureException;

}
