package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CohortRepository extends JpaRepository<CohortEntity, String> {
    Page<CohortEntity> findByNameContainingIgnoreCase(String name,Pageable pageRequest);

    Page<CohortEntity> findAllByProgramId(String programId, Pageable pageRequest);

    Page<CohortEntity> findAllByOrganizationId(String organizationId, Pageable pageRequest);

    Page<CohortEntity> findByProgramIdAndNameContainingIgnoreCase(String programId, String name,Pageable pageRequest);

    CohortEntity findByName(String name);

    Page<CohortEntity> findByOrganizationIdAndNameContainingIgnoreCase(String organizationId, String name,Pageable pageRequest);

    List<CohortEntity> findAllByProgramId(String id);
}
