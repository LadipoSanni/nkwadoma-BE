package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;

import java.util.List;

public interface CohortLoaneeOutputPort {

    CohortLoanee save(CohortLoanee cohortLoanee) throws MeedlException;

    void delete(String id) throws MeedlException;

    CohortLoanee findCohortLoaneeByLoaneeIdAndCohortId(String loaneeId, String cohortId) throws MeedlException;

    CohortLoanee findCohortLoaneeByProgramIdAndLoaneeId(String programId, String loaneeId) throws MeedlException;

    List<CohortLoanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException;
}
