package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;

import java.util.List;

public interface ProgramCohortOutputPort {
    ProgramCohort findByCohortName(String name);

    ProgramCohort findByProgramId(String programId);

    List<ProgramCohort> findAllByProgramId(String programId);

    void save(ProgramCohort programCohort1);


    void delete(String id) throws MeedlException;

    void deleteAllByCohort(CohortEntity cohort);
}
