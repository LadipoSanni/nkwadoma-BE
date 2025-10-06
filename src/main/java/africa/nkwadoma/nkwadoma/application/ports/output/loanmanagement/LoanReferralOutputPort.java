package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import java.util.*;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import org.springframework.data.domain.Page;

public interface LoanReferralOutputPort {
    LoanReferral save(LoanReferral loanReferral) throws MeedlException;

    Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException;
    LoanReferral findById(String loanReferralId) throws MeedlException;

    void deleteLoanReferral(String loanReferralId) throws MeedlException;

    List<LoanReferral> findLoanReferralByUserId(String userId) throws MeedlException;


    LoanReferral findLoanReferralByLoaneeIdAndCohortId(String id, String cohortId) throws MeedlException;

    List<LoanReferral> viewAll();

    LoanReferral findByEmail(String loaneeEmail) throws MeedlException;

    LoanReferral findLoanReferralByCohortLoaneeId(String id) throws MeedlException;

    Page<LoanReferral> findAllLoanReferralsForLoanee(String loaneeId, int pageNumber, int pageSize) throws MeedlException;

    List<LoanReferral> findAllLoanReferralsByUserIdAndStatus(String id,LoanReferralStatus loanReferralStatus) throws MeedlException;

    Page<LoanReferral> findAllLoanReferrals(LoanReferral loanReferral) throws MeedlException;

    Page<LoanReferral> searchLoanReferrals(LoanReferral request) throws MeedlException;

}
