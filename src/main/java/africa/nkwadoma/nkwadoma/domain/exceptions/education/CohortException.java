package africa.nkwadoma.nkwadoma.domain.exceptions.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;

public class CohortException extends MeedlException {
    public CohortException(String message) {
        super(message);
    }

    public CohortException(String message, Throwable cause) {
        super(message, cause);
    }

    public CohortException(Throwable cause) {
        super(cause);
    }
}
