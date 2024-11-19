package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;

import java.util.*;

public interface CohortOutputPort {

    Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException;

    Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException;

    void deleteCohort(String id) throws MeedlException;

    List<Cohort> findAllCohortInAProgram(String id) throws MeedlException;

    Cohort findCohort(String cohortId) throws CohortException;

    Cohort save(Cohort cohort) throws MeedlException;

    Cohort findCohortByName(String name) throws MeedlException;
}
