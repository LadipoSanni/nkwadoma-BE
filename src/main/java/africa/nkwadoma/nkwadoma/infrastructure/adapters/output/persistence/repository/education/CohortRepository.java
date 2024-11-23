package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CohortRepository extends JpaRepository<CohortEntity, String> {
    Optional<CohortEntity> findByProgramId(String programId);

    CohortEntity findCohortByName(String name);

    List<CohortEntity> findAllByProgramId(String programId);

    Page<CohortEntity> findAllByOrganizationId(String organizationId, Pageable pageRequest);
}
