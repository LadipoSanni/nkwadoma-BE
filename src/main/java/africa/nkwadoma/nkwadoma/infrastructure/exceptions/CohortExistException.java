package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public class CohortExistException  extends MeedlException {
    public CohortExistException(String message) {
        super(message);
    }
}
