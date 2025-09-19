package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoaneeUseCase {
    List<Loanee> inviteLoanees(List<Loanee> loanees, String cohortId);

    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

    LoaneeLoanDetail viewLoaneeLoanDetail(String cohortLoaneeId) throws MeedlException;

    void increaseNumberOfLoaneesInOrganization(Cohort cohort, int size) throws MeedlException;

    void increaseNumberOfLoaneesInProgram(Cohort cohort, int size) throws MeedlException;

    Loanee viewLoaneeDetails(String loaneeId, String userId) throws MeedlException;

    Page<CohortLoanee> viewAllLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException;

    LoanReferral referLoanee(CohortLoanee cohortLoanee) throws MeedlException;

//    void notifyLoanReferralActors(List<Loanee> loanees) throws MeedlException;

    Page<CohortLoanee> searchForLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException;

    Page<CohortLoanee> viewAllLoaneeThatBenefitedFromLoanProduct(String loanProductId, int pageSize, int pageNumber) throws MeedlException;


    Page<CohortLoanee> searchLoaneeThatBenefitedFromLoanProduct(String loanProductId,String name, int pageSize, int pageNumber) throws MeedlException;

    String indicateDeferredLoanee(String actorId, String loaneeId) throws MeedlException;

    String deferLoan(String userId, String loanId, String reasonForDeferral) throws MeedlException;

    String indicateDropOutLoanee(String userId, String loanId) throws MeedlException;

    String dropOutFromCohort(String userId, String cohortId, String reasonForDropout) throws MeedlException;

    String resumeProgram(String loanId, String cohortId, String userId) throws MeedlException;

    String archiveOrUnArchiveByIds(String cohortId, List<String> loaneeIds, LoaneeStatus loaneeStatus) throws MeedlException;

    void updateLoaneeStatus(Loanee loanee);

    CohortLoanee viewLoaneeDetailInCohort(String cohortId, String loaneeId) throws MeedlException;

    Page<LoaneeLoanAggregate> viewAllLoanee(String actorId,int pageSize, int pageNumber) throws MeedlException;

    Page<LoaneeLoanAggregate> searchLoanAggregate(Loanee loanee,int pageSize, int pageNumber) throws MeedlException;

    String updateTrainingPerformance(String trainingPerformance, String cohortId, String loaneeId) throws MeedlException;
}
