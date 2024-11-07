package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;

public interface CohortUseCase {

    Cohort createCohort(Cohort cohort) throws MeedlException;


    Cohort viewCohortDetails(String createdBy, String id, String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    void inviteCohort(String userId, String programId, String cohortId) throws MeedlException;
}
