package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CapitalDistributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapitalDistributionRepository extends JpaRepository<CapitalDistributionEntity,String> {
}
