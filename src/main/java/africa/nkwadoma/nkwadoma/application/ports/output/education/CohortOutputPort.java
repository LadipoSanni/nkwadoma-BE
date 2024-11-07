package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import org.springframework.data.domain.Page;

import java.util.*;

public interface CohortOutputPort {
    Cohort  saveCohort(Cohort cohort) throws MeedlException;

    Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException;
    List<Cohort> findAllCohortInAProgram(String id) throws MeedlException;

//    Program findProgramByName(String programName) throws ResourceNotFoundException;
//    boolean programExists(String programName) throws MeedlException;
//
//    void deleteProgram(String programId);
//
//    Program findProgramById(String programId) throws ResourceNotFoundException;
}
