package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.util.*;

public interface LoanReferralOutputPort {
    LoanReferral saveLoanReferral(LoanReferral loanReferral) throws MeedlException;

    Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException;

    void deleteLoanReferral(String loanReferralId) throws MeedlException;
}
