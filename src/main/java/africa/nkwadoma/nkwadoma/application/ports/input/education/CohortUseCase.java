package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import org.springframework.data.domain.*;

public interface CohortUseCase {


    Cohort createCohort(Cohort cohort)  throws MeedlException;
    Cohort editCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String createdBy, String id, String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    void inviteCohort(String userId, String programId, String cohortId) throws MeedlException;

    Page<Cohort> viewAllCohortInAProgram(String programId, int pageNumber, int pageSize) throws MeedlException;

    Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException;
}
