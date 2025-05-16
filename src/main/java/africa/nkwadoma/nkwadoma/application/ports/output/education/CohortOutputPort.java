package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

public interface CohortOutputPort {

    Cohort viewCohortDetails(String userId,  String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;
    Page<Cohort> findAllCohortInAProgram(Cohort cohort) throws MeedlException;

    Cohort findCohort(String cohortId) throws MeedlException;

    Cohort save(Cohort cohort) throws MeedlException;

    Page<Cohort> findCohortByNameAndOrganizationId(Cohort cohort) throws MeedlException;

    Page<Cohort> findAllCohortByOrganizationId(String organizationId,Cohort cohort) throws MeedlException;

    Page<Cohort> searchForCohortInAProgram(String name,String programId,int pageSize, int pageNumber) throws MeedlException;

    Cohort checkIfCohortExistWithName(String name) throws MeedlException;

    Page<Cohort> searchCohortInOrganization(String organizationId, String name,int pageSize,int pageNumber) throws MeedlException;
}
