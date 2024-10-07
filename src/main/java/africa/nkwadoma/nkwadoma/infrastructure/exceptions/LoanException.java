package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.validation.MiddleValidator;

public class LoanProductException extends MiddlException {
    public LoanProductException(String message) {
        super(message);
    }
}
