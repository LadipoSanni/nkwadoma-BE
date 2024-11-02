package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;

import java.util.List;

public interface ProgramCohortOutputPort {
    ProgramCohort findByCohortName(String name);

    ProgramCohort findByProgramId(String programId) throws MeedlException;

    List<ProgramCohort> findAllByProgramId(String programId) throws MeedlException;

    void save(ProgramCohort programCohort1) throws MeedlException;


    void delete(String id) throws MeedlException;
}
