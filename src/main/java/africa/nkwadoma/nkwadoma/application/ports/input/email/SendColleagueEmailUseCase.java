package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;

public interface SendColleagueEmailUseCase {
    void sendColleagueEmail(UserIdentity userIdentity) throws MeedlException;
}
