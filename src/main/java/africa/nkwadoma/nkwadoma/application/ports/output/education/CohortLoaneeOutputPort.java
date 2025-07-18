package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CohortLoaneeOutputPort {

    CohortLoanee save(CohortLoanee cohortLoanee) throws MeedlException;

    void delete(String id) throws MeedlException;

    CohortLoanee findCohortLoaneeByLoaneeIdAndCohortId(String loaneeId, String cohortId) throws MeedlException;

    CohortLoanee findCohortLoaneeByProgramIdAndLoaneeId(String programId, String loaneeId) throws MeedlException;

    List<CohortLoanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException;

    boolean checkIfLoaneeHasBeenPreviouslyReferred(String loaneeId) throws MeedlException;

    Page<CohortLoanee> findAllLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException;

    Page<CohortLoanee> searchForLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException;

    Page<CohortLoanee> findAllLoaneeThatBenefitedFromLoanProduct(String id, int pageSize, int pageNumber) throws MeedlException;

    Page<CohortLoanee> searchLoaneeThatBenefitedFromLoanProduct(String id, String name, int pageSize, int pageNumber) throws MeedlException;

    void archiveOrUnArchiveByIds(String cohortId, List<String> loaneeIds, LoaneeStatus loaneeStatus) throws MeedlException;

    CohortLoanee findCohortLoaneeByLoanRequestId(String id) throws MeedlException;
}
