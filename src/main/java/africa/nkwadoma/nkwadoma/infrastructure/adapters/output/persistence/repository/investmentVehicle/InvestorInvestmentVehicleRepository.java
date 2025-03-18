package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvestorInvestmentVehicleRepository extends JpaRepository<InvestmentVehicleFinancierEntity,String> {
    Optional<InvestmentVehicleFinancierEntity> findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId);
    @Query("SELECT ivf.financier FROM InvestmentVehicleFinancierEntity ivf WHERE ivf.investmentVehicle.id = :investmentVehicleId")
    Page<FinancierEntity> findFinanciersByInvestmentVehicleId(@Param("investmentVehicleId") String investmentVehicleId, Pageable pageable);

    @Query("SELECT ivf.financier FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle.id = :investmentVehicleId " +
            "AND ivf.financier.activationStatus = :activationStatus")
    Page<FinancierEntity> findFinanciersByInvestmentVehicleIdAndStatus(
            @Param("investmentVehicleId") String investmentVehicleId,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );

    void deleteByInvestmentVehicleIdAndFinancierId(String investmentId, String id);
}
