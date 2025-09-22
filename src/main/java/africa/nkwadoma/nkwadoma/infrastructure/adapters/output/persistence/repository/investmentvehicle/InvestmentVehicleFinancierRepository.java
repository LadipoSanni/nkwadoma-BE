package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.InvestmentVehicleFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierWithDesignationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvestmentVehicleFinancierRepository extends JpaRepository<InvestmentVehicleFinancierEntity,String> {

    @Query("SELECT ivf.financier FROM InvestmentVehicleFinancierEntity ivf WHERE ivf.investmentVehicle.id = :investmentVehicleId")
    Page<FinancierEntity> findFinanciersByInvestmentVehicleId(@Param("investmentVehicleId") String investmentVehicleId, Pageable pageable);
    Page<InvestmentVehicleFinancierEntity> findAllByInvestmentVehicle_Id(@Param("investmentVehicleId") String investmentVehicleId, Pageable pageable);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle.id = :investmentVehicleId " +
            "AND (:activationStatus IS NULL OR ivf.financier.activationStatus = :activationStatus)")
    Page<InvestmentVehicleFinancierEntity> findFinanciersByInvestmentVehicleIdAndStatus(
            @Param("investmentVehicleId") String investmentVehicleId,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );

    @Query(value = """
    WITH financier_investments AS (
        SELECT 
            ivf.financier_id,
            ivf.id AS investment_id,
            ivf.amount_invested,
            ivf.date_invested,
            CASE
                WHEN fe.financier_type = 'INDIVIDUAL' THEN CONCAT(u.first_name, ' ', u.last_name)
                WHEN fe.financier_type = 'COOPERATE' THEN o.name
                ELSE NULL
            END AS financier_name
        FROM investment_vehicle_financier_entity ivf
        JOIN financier_entity fe ON fe.id = ivf.financier_id
        LEFT JOIN organization o ON o.id = fe.identity
        LEFT JOIN meedl_user u ON u.id = fe.identity
        WHERE ivf.investment_vehicle_id = :investmentVehicleId
        AND (:activationStatus IS NULL OR fe.activation_status = :activationStatus)
    ),
    financier_designations AS (
        SELECT 
            ivf.financier_id,
            array_agg(DISTINCT ivfd.investment_vehicle_designation ORDER BY ivfd.investment_vehicle_designation) AS designations
        FROM investment_vehicle_financier_entity ivf
        JOIN investment_vehicle_financier_entity_investment_vehicle_designation ivfd 
            ON ivfd.investment_vehicle_financier_entity_id = ivf.id
        WHERE ivf.investment_vehicle_id = :investmentVehicleId
        AND ivfd.investment_vehicle_designation IS NOT NULL
        GROUP BY ivf.financier_id
    )
    SELECT 
        fi.financier_id AS financier,
        COALESCE(fd.designations, '{}') AS investment_vehicle_designation,
        fi.financier_name,
        SUM(fi.amount_invested) AS total_amount_invested,
        COUNT(fi.investment_id) AS number_of_investments,
        MAX(fi.date_invested) AS latest_date_invested
    FROM financier_investments fi
    LEFT JOIN financier_designations fd ON fd.financier_id = fi.financier_id
    GROUP BY fi.financier_id, fi.financier_name, fd.designations
""",
            countQuery = """
    SELECT COUNT(DISTINCT ivf.financier_id)
    FROM investment_vehicle_financier_entity ivf
    JOIN financier_entity fe ON fe.id = ivf.financier_id
    WHERE ivf.investment_vehicle_id = :investmentVehicleId
    AND (:activationStatus IS NULL OR fe.activation_status = :activationStatus)
""",
            nativeQuery = true)
    Page<FinancierWithDesignationProjection> findDistinctFinanciersWithDesignationByInvestmentVehicleIdAndStatus(
            @Param("investmentVehicleId") String investmentVehicleId,
            @Param("activationStatus") String activationStatus,
            Pageable pageable
    );



    void deleteByInvestmentVehicleIdAndFinancierId(String investmentId, String id);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "JOIN FETCH ivf.investmentVehicle iv " +
            "LEFT JOIN FETCH iv.operation op " +
            "WHERE ivf.financier.id = :financierId " +
            "AND ivf.amountInvested > 0")
    List<InvestmentVehicleFinancierEntity> findAllInvestmentVehicleFinancierInvestedIn(String financierId);

    List<InvestmentVehicleFinancierEntity> findAllByInvestmentVehicle_IdAndFinancier_Id(String investmentVehicleId, String financierId);


    @Query("SELECT CASE WHEN COUNT(ivf) > 0 THEN true ELSE false END " +
            "FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle.id = :investmentVehicleId " +
            "AND ivf.amountInvested IS NOT NULL " +
            "AND ivf.amountInvested > 0")
    boolean checkIfAnyFinancierAlreadyInvestedInVehicle(@Param("investmentVehicleId") String investmentVehicleId);

    void deleteByInvestmentVehicleId(String investmentVehicleId);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "JOIN FETCH ivf.investmentVehicle iv " +
            "LEFT JOIN FETCH ivf.investmentVehicleDesignation " +
            "WHERE ivf.financier.id = :financierId " +
            "AND ivf.id = :investmentVehicleFinancierId")
    InvestmentVehicleFinancierEntity findByFinancierIdAndInvestmentVehicleFinancierId(String financierId, String investmentVehicleFinancierId);

    @Query("SELECT CASE WHEN COUNT(ivf) > 0 THEN true ELSE false END " +
            "FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.investmentVehicle.id = :investmentVehicleId ")
    boolean checkIfAnyFinancierExistInVehicle(String investmentVehicleId);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf WHERE ivf.financier.identity = :userId ")
    Page<InvestmentVehicleFinancierEntity> findAllInvestmentVehicleFinancierInvestedInByUserId(String userId, Pageable pageRequest);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf WHERE ivf.financier.id = :finanacierId ")
    Page<InvestmentVehicleFinancierEntity> findAllInvestmentVehicleFinancierInvestedInByFinancierId(String finanacierId, Pageable pageRequest);

    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.financier.identity = :userId " +
            "AND LOWER(ivf.investmentVehicle.name) LIKE LOWER(CONCAT('%', :investmentVehicleName, '%'))")
    Page<InvestmentVehicleFinancierEntity> searchFinancierInvestmentByInvestmentVehicleNameAndUserId(
            @Param("investmentVehicleName") String investmentVehicleName,
            @Param("userId") String userId,
            Pageable pageRequest);


    @Query("SELECT ivf FROM InvestmentVehicleFinancierEntity ivf " +
            "WHERE ivf.financier.id = :financierId " +
            "AND LOWER(ivf.investmentVehicle.name) LIKE LOWER(CONCAT('%', :investmentVehicleName, '%'))")
    Page<InvestmentVehicleFinancierEntity> searchFinancierInvestmentByInvestmentVehicleNameAndFinancierId(
            @Param("investmentVehicleName") String investmentVehicleName,
            @Param("financierId") String financierId, Pageable pageRequest);

    int countByFinancier_IdAndInvestmentVehicle_Id(String financierId, String investmentVehicleId);

    @Query("""
        SELECT ivf
        FROM InvestmentVehicleFinancierEntity ivf
        WHERE ivf.financier.id = :financierId
        ORDER BY ivf.dateInvested DESC
        """)
    Optional<InvestmentVehicleFinancierEntity> findRecentInvestmentVehicleFinancierIsAddedTo(String financierId);
}
