package africa.nkwadoma.nkwadoma.domain.exceptions;

public class IdentityException extends MiddlException {

    public IdentityException(String message){
        super(message);
    }

    public IdentityException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityException(Throwable cause) {
        super(cause);
    }


}
