package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorInvestmentVehicleRepository extends JpaRepository<InvestmentVehicleFinancierEntity,String> {
    Optional<InvestmentVehicleFinancierEntity> findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId);
}
