package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.VehicleClosureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleClosureRepository extends JpaRepository<VehicleClosureEntity,String> {
}
