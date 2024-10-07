package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;

public class LoanException extends MiddlException {
    public LoanException(String message) {
        super(message);
    }
}
