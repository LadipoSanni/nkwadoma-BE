package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

public interface SendColleagueEmailUseCase {
    void sendColleagueEmail(UserIdentity userIdentity) throws MiddlException;
}
