package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.VehicleOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleOperationRepository extends JpaRepository<VehicleOperationEntity, String> {
}
