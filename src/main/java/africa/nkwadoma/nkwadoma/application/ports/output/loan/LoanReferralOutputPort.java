package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.util.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

public interface LoanReferralOutputPort {
    LoanReferral saveLoanReferral(LoanReferral loanReferral) throws MeedlException;

    Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException;

    void deleteLoanReferral(String loanReferralId) throws MeedlException;
    LoanReferral createLoanReferral(Loanee loanee) throws MeedlException;
}
