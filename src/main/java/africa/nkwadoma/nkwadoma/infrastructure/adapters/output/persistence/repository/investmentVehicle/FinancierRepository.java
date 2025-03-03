package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

}
