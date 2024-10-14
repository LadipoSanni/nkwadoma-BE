package africa.nkwadoma.nkwadoma.domain.exceptions;

public class MeedlException extends Exception {

    public MeedlException(String message) {
        super(message);
    }

    public MeedlException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeedlException(Throwable cause) {
        super(cause);
    }
}
