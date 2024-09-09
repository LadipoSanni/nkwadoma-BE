package africa.nkwadoma.nkwadoma.application.ports.input;

import africa.nkwadoma.nkwadoma.domain.exceptions.LearnSpaceUserException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;

public interface UserService {
    UserIdentity createUser(UserIdentity userIdentity) throws LearnSpaceUserException;
}
