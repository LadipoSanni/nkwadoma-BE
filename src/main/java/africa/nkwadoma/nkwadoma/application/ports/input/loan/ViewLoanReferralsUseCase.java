package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface ViewLoanReferralsUseCase {
    LoanReferral viewLoanReferral(LoanReferral loanReferral) throws MeedlException;
}
