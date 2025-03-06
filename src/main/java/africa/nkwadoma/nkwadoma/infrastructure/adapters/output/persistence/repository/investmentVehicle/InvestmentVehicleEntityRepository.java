package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvestmentVehicleEntityRepository extends JpaRepository<InvestmentVehicleEntity,String> {


    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.name = :name AND i.investmentVehicleStatus <> :status")
    InvestmentVehicleEntity findByNameAndStatusNotDraft(String name, InvestmentVehicleStatus status);

    List<InvestmentVehicleEntity> findAllByNameContainingIgnoreCase(String name);

    Page<InvestmentVehicleEntity> findByInvestmentVehicleType(InvestmentVehicleType type, Pageable pageable);
}
