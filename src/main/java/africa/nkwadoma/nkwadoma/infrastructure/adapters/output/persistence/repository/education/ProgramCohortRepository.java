package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramCohortRepository extends JpaRepository<ProgramCohortEntity, String> {

    ProgramCohortEntity findByProgramId(String programId);

    List<ProgramCohortEntity> findAllByProgramId(String programId);

    void deleteAllByProgramId(String id);

    void deleteAllByCohort(CohortEntity cohort);
}
