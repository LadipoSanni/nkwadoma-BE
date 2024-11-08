package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;

public interface CohortOutputPort {
    Cohort  saveCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException;

    Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;
}
