package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

public interface LoanReferralOutputPort {
    LoanReferral saveLoanReferral(LoanReferral loanReferral) throws MeedlException;

    Page<LoanReferral> findLoanReferrals(String loaneeId, int pageNumber, int pageSize) throws MeedlException;

    void deleteLoanReferral(String loanReferralId) throws MeedlException;
}
