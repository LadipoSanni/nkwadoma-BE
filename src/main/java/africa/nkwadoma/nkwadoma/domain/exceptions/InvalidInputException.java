package africa.nkwadoma.nkwadoma.domain.exceptions;

public class InvalidInputException extends MeedlException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
