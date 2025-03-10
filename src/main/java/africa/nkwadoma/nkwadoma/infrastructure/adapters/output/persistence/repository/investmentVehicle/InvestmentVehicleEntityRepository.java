package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvestmentVehicleEntityRepository extends JpaRepository<InvestmentVehicleEntity,String> {


    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.name = :name AND i.investmentVehicleStatus <> :status")
    InvestmentVehicleEntity findByNameAndStatusNotDraft(String name, InvestmentVehicleStatus status);

    List<InvestmentVehicleEntity> findAllByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.investmentVehicleType = :type AND i.investmentVehicleStatus = 'PUBLISHED' ORDER BY i.startDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleType(@Param("type") InvestmentVehicleType type, Pageable pageable);

    List<InvestmentVehicleEntity> findByInvestmentVehicleStatus(InvestmentVehicleStatus investmentVehicleStatus);

    @Query("SELECT v FROM InvestmentVehicleEntity v " +
            "WHERE v.investmentVehicleType = :type " +
            "AND v.investmentVehicleStatus = :status")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleTypeAndStatus(
            @Param("type") InvestmentVehicleType type,
            @Param("status") InvestmentVehicleStatus status,
            Pageable pageable
    );

    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.fundRaisingStatus = :fundRaisingStatus AND i.investmentVehicleStatus = 'PUBLISHED' ORDER BY i.startDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleByFundRaisingStatus(FundRaisingStatus fundRaisingStatus, Pageable pageRequest);

}
