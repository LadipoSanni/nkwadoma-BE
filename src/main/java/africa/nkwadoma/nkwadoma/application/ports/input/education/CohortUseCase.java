package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

public interface CohortUseCase {

    Cohort createCohort(Cohort cohort) throws MeedlException;


    Cohort viewCohortDetails(String createdBy, String id, String cohortId) throws MeedlException;
    Page<Cohort> viewAllCohortInAProgram(String id, int pageSize, int pageNumber) throws MeedlException;

}
