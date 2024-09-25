package africa.nkwadoma.nkwadoma.application.ports.input;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;

public interface CreateUserUseCase {
    UserIdentity createUser(UserIdentity userIdentity) throws MiddlException, InfrastructureException;
}
