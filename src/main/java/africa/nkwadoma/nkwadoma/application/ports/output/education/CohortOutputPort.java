package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

public interface CohortOutputPort {
    Cohort  saveCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException;
    Page<Cohort> findAllCohortInAProgram(String id, int pageSize, int pageNumber) throws MeedlException;

//    Program findProgramByName(String programName) throws ResourceNotFoundException;
//    boolean programExists(String programName) throws MeedlException;
//
//    void deleteProgram(String programId);
//
//    Program findProgramById(String programId) throws ResourceNotFoundException;
}
