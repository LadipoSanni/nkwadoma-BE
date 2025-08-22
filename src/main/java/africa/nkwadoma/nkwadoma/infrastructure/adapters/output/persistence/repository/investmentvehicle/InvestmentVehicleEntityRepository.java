package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.InvestmentVehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    Optional<InvestmentVehicleEntity> findByInvestmentVehicleLink(String investmentVehicleLink);

    @Query("SELECT i FROM InvestmentVehicleEntity i WHERE i.fundRaisingStatus = :fundRaisingStatus AND i.investmentVehicleStatus = 'PUBLISHED' ORDER BY i.createdDate DESC")
    Page<InvestmentVehicleEntity> findByInvestmentVehicleByFundRaisingStatus(FundRaisingStatus fundRaisingStatus, Pageable pageRequest);

    @Query("SELECT i FROM InvestmentVehicleEntity i " +
            "WHERE i.investmentVehicleVisibility != 'DEFAULT' " +
            "AND (i.investmentVehicleVisibility = 'PUBLIC' " +
            "OR (i.investmentVehicleVisibility = 'PRIVATE' " +
            "AND EXISTS (SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle = i " +
            "AND ivf.financier.identity = :userId)))")
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
            "                  AND ivf.financier.identity = :userId))" +
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
            "AND ivf.financier.identity = :userId))) " +
            "AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType) " +
            "AND i.investmentVehicleStatus = :investmentVehicleStatus " +
            "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<InvestmentVehicleEntity> findAllByNameContainingIgnoreCaseAndInvestmentVehicleTypeAndStatusExcludingPrivateAndDefault(
            @Param("userId") String userId,
            @Param("investmentVehicleStatus") InvestmentVehicleStatus investmentVehicleStatus,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("name") String name,
            Pageable pageRequest);


    @Query("""
    SELECT
        i.id AS id,
        i.name AS name,
        i.investmentVehicleType AS investmentVehicleType,
        i.mandate AS mandate,
        i.investmentVehicleStatus AS investmentVehicleStatus,
        i.size AS size,
        i.tenure AS tenure,
        i.totalAvailableAmount AS totalAvailableAmount,
        i.interestRateOffered AS interestRateOffered,
        i.investmentVehicleVisibility AS investmentVehicleVisibility,
        i.trustee AS trustee,
        i.custodian AS custodian,
        i.bankPartner AS bankPartner,
        i.fundManager AS fundManager,
        i.startDate AS startDate,
        i.createdDate AS createdDate,
        i.investmentVehicleLink AS investmentVehicleLink,
        i.minimumInvestmentAmount As minimumInvestmentAmount,
        i.talentFunded as talentFunded,
        COALESCE(SUM(ivf.amountInvested), 0) AS amountFinancierInvested,
        vo.couponDistributionStatus AS couponDistributionStatus,
        vo.fundRaisingStatus AS fundRaising,
        vo.deployingStatus AS deployingStatus,
        vc.recollectionStatus as recollectionStatus,
        vc.maturity as maturity
    FROM InvestmentVehicleEntity i
    LEFT JOIN InvestmentVehicleFinancierEntity ivf ON ivf.investmentVehicle = i
    LEFT JOIN i.operation vo
    LEFT JOIN i.closure vc
    WHERE ivf.financier.identity = :userId
    AND i.investmentVehicleStatus = 'PUBLISHED'
    AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType)
        GROUP BY
                i.id,
                i.name,
                i.investmentVehicleType,
                i.mandate,
                i.investmentVehicleStatus,
                i.size,
                i.tenure,
                i.totalAvailableAmount,
                i.interestRateOffered,
                i.investmentVehicleVisibility,
                i.trustee,
                i.custodian,
                i.bankPartner,
                i.fundManager,
                i.startDate,
                i.createdDate,
                i.investmentVehicleLink,
                i.talentFunded,
                vo.couponDistributionStatus,
                vo.fundRaisingStatus,
                vo.deployingStatus,
                vc.recollectionStatus,
                vc.maturity
""")
    Page<InvestmentVehicleProjection> findAllInvestmentVehicleFinancierWasAddedToByInvestmentVehicleType(
            @Param("userId") String userId,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType, Pageable pageRequest);


    @Query("""
    SELECT
        i.id AS id,
        i.name AS name,
        i.investmentVehicleType AS investmentVehicleType,
        i.mandate AS mandate,
        i.investmentVehicleStatus AS investmentVehicleStatus,
        i.size AS size,
        i.tenure AS tenure,
        i.totalAvailableAmount AS totalAvailableAmount,
        i.interestRateOffered AS interestRateOffered,
        i.investmentVehicleVisibility AS investmentVehicleVisibility,
        i.trustee AS trustee,
        i.custodian AS custodian,
        i.bankPartner AS bankPartner,
        i.fundManager AS fundManager,
        i.startDate AS startDate,
        i.createdDate AS createdDate,
        i.investmentVehicleLink AS investmentVehicleLink,
        i.minimumInvestmentAmount As minimumInvestmentAmount,
        i.talentFunded as talentFunded,
        COALESCE(SUM(ivf.amountInvested), 0) AS amountFinancierInvested,
        vo.couponDistributionStatus AS couponDistributionStatus,
        vo.fundRaisingStatus AS fundRaising,
        vo.deployingStatus AS deployingStatus,
        vc.recollectionStatus as recollectionStatus,
        vc.maturity as maturity
    FROM InvestmentVehicleEntity i
    LEFT JOIN InvestmentVehicleFinancierEntity ivf ON ivf.investmentVehicle = i
    LEFT JOIN i.operation vo
    LEFT JOIN i.closure vc
    WHERE ivf.financier.identity = :userId
    AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:investmentVehicleType IS NULL OR i.investmentVehicleType = :investmentVehicleType)
        GROUP BY
                i.id,
                i.name,
                i.investmentVehicleType,
                i.mandate,
                i.investmentVehicleStatus,
                i.size,
                i.tenure,
                i.totalAvailableAmount,
                i.interestRateOffered,
                i.investmentVehicleVisibility,
                i.talentFunded, 
                i.trustee,
                i.custodian,
                i.bankPartner,
                i.fundManager,
                i.startDate,
                i.createdDate,
                i.investmentVehicleLink,
                vo.couponDistributionStatus,
                vo.fundRaisingStatus,
                vo.deployingStatus,
                vc.recollectionStatus,
                vc.maturity

""")
    Page<InvestmentVehicleProjection> findAllInvestmentVehicleFinancierWasAddedToByVehicleNameContainingIgnoreCaseAndInvestmentVehicleType(
            @Param("userId") String userId,
            @Param("investmentVehicleType") InvestmentVehicleType investmentVehicleType,
            @Param("name") String name,
            Pageable pageRequest);


    @Query("""
    SELECT
        i.id AS id,
        i.name AS name,
        i.investmentVehicleType AS investmentVehicleType,
        i.mandate AS mandate,
        i.investmentVehicleStatus AS investmentVehicleStatus,
        i.size AS size,
        i.tenure AS tenure,
        i.totalAvailableAmount AS totalAvailableAmount,
        i.interestRateOffered AS interestRateOffered,
        i.investmentVehicleVisibility AS investmentVehicleVisibility,
        i.trustee AS trustee,
        i.custodian AS custodian,
        i.bankPartner AS bankPartner,
        i.fundManager AS fundManager,
        i.startDate AS startDate,
        i.createdDate AS createdDate,
        i.investmentVehicleLink AS investmentVehicleLink,
        i.minimumInvestmentAmount As minimumInvestmentAmount,
        i.talentFunded as talentFunded,
        COALESCE(SUM(ivf.amountInvested), 0) AS amountFinancierInvested,
        vo.couponDistributionStatus AS couponDistributionStatus,
        vo.fundRaisingStatus AS fundRaising,
        vo.deployingStatus AS deployingStatus,
        vc.recollectionStatus as recollectionStatus,
        vc.maturity as maturity
    FROM InvestmentVehicleEntity i
    LEFT JOIN InvestmentVehicleFinancierEntity ivf ON ivf.investmentVehicle = i
    LEFT JOIN i.operation vo
    LEFT JOIN i.closure vc
    WHERE ivf.financier.id = :financierId
    AND i.investmentVehicleStatus = 'PUBLISHED'
    GROUP BY
            i.id,
            i.name,
            i.investmentVehicleType,
            i.mandate,
            i.investmentVehicleStatus,
            i.size,
            i.tenure,
            i.totalAvailableAmount,
            i.interestRateOffered,
            i.investmentVehicleVisibility,
            i.trustee,
            i.custodian,
            i.bankPartner,
            i.talentFunded,
            i.fundManager,
            i.startDate,
            i.createdDate,
            i.investmentVehicleLink,
            vo.couponDistributionStatus,
            vo.fundRaisingStatus,
            vo.deployingStatus,
            vc.recollectionStatus,
            vc.maturity
""")
    Page<InvestmentVehicleProjection> findAllInvestmentVehicleFinancierWasAddedToByFinancierId(
            @Param("financierId")String financierId, Pageable pageRequest);

    boolean existsByName(String investmentVehicleName);

    boolean existsByInvestmentVehicleLink(String investmentVehicleLink);

    @Query("""
            SELECT iv FROM InvestmentVehicleEntity iv
            WHERE iv.id = (SELECT lp.investmentVehicleId FROM LoanOfferEntity lo
            JOIN lo.loanProduct lp WHERE lo.id = :loanOfferId)
            """)
    InvestmentVehicleEntity findByLoanOfferId(@Param("loanOfferId") String loanOfferId);

    @Query("""
    SELECT
        i.id AS id,
        i.name AS name,
        i.investmentVehicleType AS investmentVehicleType,
        i.mandate AS mandate,
        i.investmentVehicleStatus AS investmentVehicleStatus,
        i.size AS size,
        i.tenure AS tenure,
        i.totalAvailableAmount AS totalAvailableAmount,
        i.interestRateOffered AS interestRateOffered,
        i.investmentVehicleVisibility AS investmentVehicleVisibility,
        i.trustee AS trustee,
        i.custodian AS custodian,
        i.bankPartner AS bankPartner,
        i.fundManager AS fundManager,
        i.startDate AS startDate,
        i.createdDate AS createdDate,
        i.investmentVehicleLink AS investmentVehicleLink,
        i.minimumInvestmentAmount As minimumInvestmentAmount,
        i.talentFunded as talentFunded,
        COALESCE(SUM(ivf.amountInvested), 0) AS amountFinancierInvested,
        vo.couponDistributionStatus AS couponDistributionStatus,
        vo.fundRaisingStatus AS fundRaising,
        vo.deployingStatus AS deployingStatus,
        vc.recollectionStatus as recollectionStatus,
        vc.maturity as maturity
    FROM InvestmentVehicleEntity i
    LEFT JOIN InvestmentVehicleFinancierEntity ivf ON ivf.investmentVehicle = i
    LEFT JOIN i.operation vo
    LEFT JOIN i.closure vc
    WHERE ivf.financier.id = :financierId
    AND i.investmentVehicleStatus = 'PUBLISHED'
    GROUP BY
            i.id,
            i.name,
            i.investmentVehicleType,
            i.mandate,
            i.investmentVehicleStatus,
            i.size,
            i.tenure,
            i.totalAvailableAmount,
            i.interestRateOffered,
            i.investmentVehicleVisibility,
            i.trustee,
            i.custodian,
            i.bankPartner,
            i.talentFunded,
            i.fundManager,
            i.startDate,
            i.createdDate,
            i.investmentVehicleLink,
            vo.couponDistributionStatus,
            vo.fundRaisingStatus,
            vo.deployingStatus,
            vc.recollectionStatus,
            vc.maturity
""")
    List<InvestmentVehicleProjection> findListOfInvestmentVehicleFinancierWasAddedToByFinancierId(
            @Param("financierId")String financierId);
}
