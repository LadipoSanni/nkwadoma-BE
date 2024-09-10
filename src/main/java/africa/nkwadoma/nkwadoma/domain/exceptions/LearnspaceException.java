package africa.nkwadoma.nkwadoma.domain.exceptions;

public class LearnspaceException extends Exception{
    public LearnspaceException() {
    }

    public LearnspaceException(String message) {
        super(message);
    }

    public LearnspaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LearnspaceException(Throwable cause) {
        super(cause);
    }


}
