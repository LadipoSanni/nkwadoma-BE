package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.VehicleClosureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleClosureRepository extends JpaRepository<VehicleClosureEntity,String> {
}
