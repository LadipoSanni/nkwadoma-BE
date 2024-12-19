package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;

import java.util.*;

public interface ProgramCohortOutputPort {
    ProgramCohort findByCohortName(String name);

    ProgramCohort findByProgramId(String programId) throws MeedlException;

    List<ProgramCohort> findAllByProgramId(String programId) throws MeedlException;

    void save(ProgramCohort programCohort1) throws MeedlException;


    void delete(String id) throws MeedlException;

    void deleteAllByCohort(CohortEntity cohort);
}
