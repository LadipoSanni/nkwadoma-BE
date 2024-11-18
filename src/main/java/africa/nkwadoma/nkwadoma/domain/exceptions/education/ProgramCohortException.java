package africa.nkwadoma.nkwadoma.domain.exceptions.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;

public class ProgramCohortException extends MeedlException {
    public ProgramCohortException(String message) {
        super(message);
    }

    public ProgramCohortException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramCohortException(Throwable cause) {
        super(cause);
    }
}
