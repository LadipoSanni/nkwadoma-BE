package africa.nkwadoma.nkwadoma.domain.exceptions;

public class ProgramException extends MiddlException{
    public ProgramException(String message) {
        super(message);
    }

    public ProgramException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramException(Throwable cause) {
        super(cause);
    }
}
