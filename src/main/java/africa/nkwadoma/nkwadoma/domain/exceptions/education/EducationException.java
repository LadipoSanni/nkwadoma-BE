package africa.nkwadoma.nkwadoma.domain.exceptions.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public class EducationException extends MeedlException {
    public EducationException(String message) {
        super(message);
    }

    public EducationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EducationException(Throwable cause) {
        super(cause);
    }
}
