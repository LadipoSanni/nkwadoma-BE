package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanReferralOutputPort {
    LoanReferral saveLoanReferral(LoanReferral loanReferral);
}
