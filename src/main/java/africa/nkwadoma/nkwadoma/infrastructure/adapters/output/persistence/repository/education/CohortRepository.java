package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CohortRepository extends JpaRepository<CohortEntity, String> {
    List<CohortEntity> findByNameContainingIgnoreCase(String name);

    Page<CohortEntity> findAllByProgramId(String programId, Pageable pageRequest);

    Page<CohortEntity> findAllByOrganizationId(String organizationId, Pageable pageRequest);

    List<CohortEntity> findByProgramIdAndNameContainingIgnoreCase(String programId, String name);

    CohortEntity findByName(String name);

    List<CohortEntity> findByOrganizationIdAndNameContainingIgnoreCase(String organizationId, String name);

    List<CohortEntity> findAllByProgramId(String id);
}
