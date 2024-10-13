package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public class LoanException extends MeedlException {
    public LoanException(String message) {
        super(message);
    }
}
