package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CouponDistributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponDistributionRepository extends JpaRepository<CouponDistributionEntity,String> {

}
