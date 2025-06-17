package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.VehicleOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleOperationRepository extends JpaRepository<VehicleOperationEntity, String> {
}
