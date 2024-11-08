package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

public interface CohortUseCase {

    Cohort createOrEditCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String createdBy, String id, String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    void inviteCohort(String userId, String programId, String cohortId) throws MeedlException;
    Page<Cohort> viewAllCohortInAProgram(Cohort cohort) throws MeedlException;
    Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException;
}
