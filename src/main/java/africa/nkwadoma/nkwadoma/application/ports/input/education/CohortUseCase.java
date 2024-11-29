package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CohortUseCase {


    Cohort createCohort(Cohort cohort)  throws MeedlException;
    Cohort editCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String createdBy,  String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    void inviteCohort(String userId, String programId, String cohortId) throws MeedlException;
    Page<Cohort> viewAllCohortInAProgram(String programId, int pageSize,int pageNumber) throws MeedlException;
    List<Cohort> searchForCohortInAProgram(String name, String programId) throws MeedlException;

    List<Cohort> searchForCohort(String userId,String name) throws MeedlException;
    Page<Cohort> viewAllCohortInOrganization(String actorId,int pageNumber,int pageSize) throws MeedlException ;
}
