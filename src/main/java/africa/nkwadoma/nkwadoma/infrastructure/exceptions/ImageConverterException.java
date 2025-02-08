package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

public class ImageConverterException extends InfrastructureException{
    public ImageConverterException(String message) {
        super(message);
    }

    public ImageConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageConverterException(Throwable cause) {
        super(cause);
    }
}
