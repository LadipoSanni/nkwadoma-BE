package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanReferralOutputPort {
    LoanReferral saveLoanReferral(LoanReferral loanReferral) throws MeedlException;

    LoanReferral findLoanReferralByLoaneeId(String loaneeId) throws MeedlException;

    void deleteLoanReferral(String loanReferralId) throws MeedlException;
}
