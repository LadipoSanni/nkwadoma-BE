package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.InstituteMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituteMetricsRepository extends JpaRepository<InstituteMetricsEntity, String> {
    InstituteMetricsEntity findByOrganizationId(String id);
}
