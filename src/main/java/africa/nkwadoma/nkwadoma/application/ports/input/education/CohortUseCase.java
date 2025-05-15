package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CohortUseCase {


    Cohort createCohort(Cohort cohort)  throws MeedlException;
    Cohort editCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String createdBy,  String cohortId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    String inviteCohort(String userId, String cohortId, List<String> loaneeIds) throws MeedlException;
    Page<Cohort> viewAllCohortInAProgram(String programId, int pageSize,int pageNumber) throws MeedlException;

    Page<Cohort> searchForCohort(String userId, Cohort cohort) throws MeedlException;
    Page<Cohort> viewAllCohortInOrganization(String actorId, Cohort cohort) throws MeedlException ;

    List<LoanBreakdown> getCohortLoanBreakDown( String cohortId) throws MeedlException;
}
