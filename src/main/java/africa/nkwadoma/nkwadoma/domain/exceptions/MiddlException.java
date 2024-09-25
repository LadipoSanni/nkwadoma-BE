package africa.nkwadoma.nkwadoma.domain.exceptions;

public class MiddlException extends Exception {

    public MiddlException(String message) {
        super(message);
    }

    public MiddlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MiddlException(Throwable cause) {
        super(cause);
    }
}
