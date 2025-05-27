package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoaneeUseCase {

    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

    Loanee viewLoaneeDetails(String loaneeId, String userId) throws MeedlException;

    Page<Loanee> viewAllLoaneeInCohort(String cohortId,int pageSize ,int pageNumber, LoaneeStatus loaneeStatus) throws MeedlException;
    LoanReferral referLoanee(Loanee loanee) throws MeedlException;

    void notifyLoanReferralActors(List<Loanee> loanees) throws MeedlException;

    List<Loanee> searchForLoaneeInCohort(String name, String cohortId) throws MeedlException;

    Page<Loanee> viewAllLoaneeThatBenefitedFromLoanProduct(String loanProductId,int pageSize,int pageNumber) throws MeedlException;


    Page<Loanee> searchLoaneeThatBenefitedFromLoanProduct(String loanProductId,String name, int pageSize, int pageNumber) throws MeedlException;

    String indicateDeferredLoanee(String actorId, String loaneeId) throws MeedlException;

    String deferProgram(Loanee loanee, String userId) throws MeedlException;

    String indicateDropOutLoanee(String actorId, String loaneeID) throws MeedlException;

    String resumeProgram(String loanId, String cohortId, String userId) throws MeedlException;

    String archiveOrUnArchiveByIds(String actorId, List<String> loaneeIds, LoaneeStatus loaneeStatus) throws MeedlException;
}
