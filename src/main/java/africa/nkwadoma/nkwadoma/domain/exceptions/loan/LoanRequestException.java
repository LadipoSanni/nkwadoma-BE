package africa.nkwadoma.nkwadoma.domain.exceptions.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public class LoanRequestException extends MeedlException {
    public LoanRequestException(String message) {
        super(message);
    }
}
