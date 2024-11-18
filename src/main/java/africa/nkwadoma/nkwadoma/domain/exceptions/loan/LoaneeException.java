package africa.nkwadoma.nkwadoma.domain.exceptions.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;

public class LoaneeException extends MeedlException {


    public LoaneeException(String message) {
        super(message);
    }

    public LoaneeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoaneeException(Throwable cause) {
        super(cause);
    }
}
