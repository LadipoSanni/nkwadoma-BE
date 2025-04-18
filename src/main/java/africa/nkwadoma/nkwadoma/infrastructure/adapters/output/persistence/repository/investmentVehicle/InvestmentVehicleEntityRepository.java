package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvestmentVehicleEntityRepository extends JpaRepository<InvestmentVehicleEntity,String> {


    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.name = :name AND i.investmentVehicleStatus <> :status")
    InvestmentVehicleEntity findByNameAndStatusNotDraft(String name, InvestmentVehicleStatus status);

    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType) " +
            "AND i.investmentVehicleStatus = :investmentVehicleStatus ")
    Page<InvestmentVehicleEntity> findAllByNameContainingIgnoreCaseAndInvestmentVehicleTypeAndStaus(
            @Param("name") String name,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("investmentVehicleStatus") InvestmentVehicleStatus investmentVehicleStatus,
            Pageable pageable);

    @Query("SELECT i FROM InvestmentVehicleEntity  i WHERE i.investmentVehicleType = :type AND i.investmentVehicleStatus = 'PUBLISHED' ORDER BY i.createdDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleType(@Param("type") InvestmentVehicleType type, Pageable pageable);

    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.investmentVehicleStatus = :investmentVehicleStatus ORDER BY i.lastUpdatedDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleStatus(InvestmentVehicleStatus investmentVehicleStatus, Pageable pageable);

    @Query("SELECT v FROM InvestmentVehicleEntity v " +
            "WHERE v.investmentVehicleType = :type " +
            "AND v.investmentVehicleStatus = :status")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleTypeAndStatus(
            @Param("type") InvestmentVehicleType type,
            @Param("status") InvestmentVehicleStatus status,
            Pageable pageable
    );

    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.fundRaisingStatus = :fundRaisingStatus AND i.investmentVehicleStatus = 'PUBLISHED' ORDER BY i.createdDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleByFundRaisingStatus(FundRaisingStatus fundRaisingStatus, Pageable pageRequest);

    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "WHERE i.investmentVehicleVisibility != 'DEFAULT' " +
            "AND (i.investmentVehicleVisibility = 'PUBLIC' " +
            "OR (i.investmentVehicleVisibility = 'PRIVATE' " +
            "AND EXISTS (SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle = i " +
            "AND ivf.financier.userIdentity.id = :userId)))")
    Page<InvestmentVehicleEntity> findAllInvestmentVehicleExcludingPrivate(
            @Param("userId") String userId,Pageable pageRequest);

    @Query("SELECT v FROM InvestmentVehicleEntity v " +
            "LEFT JOIN VehicleOperationEntity vo ON v.operation.id = vo.id " +
            "WHERE (:investmentVehicleType IS NULL OR v.investmentVehicleType = :investmentVehicleType) " +
            "AND (:investmentVehicleStatus IS NULL OR v.investmentVehicleStatus = :investmentVehicleStatus) " +
            "AND (:investmentVehicleMode IS NULL OR vo.fundRaisingStatus = :investmentVehicleMode OR vo IS NULL)")
    Page<InvestmentVehicleEntity> findAllInvestmentVehicleBy(
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("investmentVehicleStatus") InvestmentVehicleStatus investmentVehicleStatus,
            @Param("investmentVehicleMode") InvestmentVehicleMode investmentVehicleMode,
            Pageable pageable);

    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "LEFT JOIN VehicleOperationEntity vo ON i.operation.id = vo.id " +
            "WHERE (" +
            "  (i.investmentVehicleStatus = 'PUBLISHED' AND i.investmentVehicleVisibility = 'PUBLIC') " +
            "  OR (i.investmentVehicleVisibility = 'PRIVATE' " +
            "      AND EXISTS (SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "                  WHERE ivf.investmentVehicle.id = i.id " +
            "                  AND ivf.financier.userIdentity.id = :userId))" +
            ") " +
            "AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType) " +
            "AND (:investmentVehicleStatus IS NULL OR i.investmentVehicleStatus = :investmentVehicleStatus) " +
            "AND (:investmentVehicleMode IS NULL OR vo.fundRaisingStatus = :investmentVehicleMode OR vo IS NULL)")
    Page<InvestmentVehicleEntity> findAllInvestmentVehicleForFinancier(
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("investmentVehicleStatus") InvestmentVehicleStatus investmentVehicleStatus,
            @Param("investmentVehicleMode") InvestmentVehicleMode investmentVehicleMode,
            @Param("userId") String userId,
            Pageable pageable);

    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "WHERE (i.investmentVehicleVisibility NOT IN ('PRIVATE', 'DEFAULT') " +
            "OR (i.investmentVehicleVisibility = 'PRIVATE' " +
            "AND EXISTS (SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle = i " +
            "AND ivf.financier.userIdentity.id = :userId))) " +
            "AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType) " +
            "AND i.investmentVehicleStatus = :investmentVehicleStatus " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<InvestmentVehicleEntity> findAllByNameContainingIgnoreCaseAndInvestmentVehicleTypeAndStatusExcludingPrivateAndDefault(
            @Param("userId") String userId,
            @Param("investmentVehicleStatus") InvestmentVehicleStatus investmentVehicleStatus,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("name") String name,
            Pageable pageRequest);


    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "WHERE EXISTS (SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle = i " +
            "AND ivf.financier.userIdentity.id = :userId) " +
            "AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType) " +
            "AND i.investmentVehicleStatus = 'PUBLISHED' " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<InvestmentVehicleEntity> findAllInvestmentVehicleFinancierWasAddedToByVehicleNameContainingIgnoreCaseAndInvestmentVehicleType(
            @Param("userId") String userId,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("name") String name,
            Pageable pageRequest);
}
