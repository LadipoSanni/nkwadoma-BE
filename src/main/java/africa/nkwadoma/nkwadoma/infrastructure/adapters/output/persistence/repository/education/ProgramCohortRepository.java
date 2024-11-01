package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramCohortRepository extends JpaRepository<ProgramCohortEntity, String> {

    ProgramCohortEntity findByProgram(String programId);

    List<ProgramCohortEntity> findAllByProgram(String programId);

}
