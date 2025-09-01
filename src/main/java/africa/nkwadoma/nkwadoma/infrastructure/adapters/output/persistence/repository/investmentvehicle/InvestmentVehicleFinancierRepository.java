package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.InvestmentVehicleFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierWithDesignationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
  SELECT ivf.financier AS financier,
         d AS investmentVehicleDesignation,
         CASE
             WHEN financierEntity.financierType = 'INDIVIDUAL' THEN CONCAT(user.firstName, ' ', user.lastName)
             WHEN financierEntity.financierType = 'COOPERATE' THEN organization.name
             ELSE NULL
         END AS financierName,
         SUM(ivf.amountInvested) AS totalAmountInvested,
         COUNT(ivf) AS numberOfInvestments
  FROM InvestmentVehicleFinancierEntity ivf
  JOIN ivf.investmentVehicleDesignation d
  JOIN FinancierEntity financierEntity ON financierEntity.id = ivf.financier.id
  LEFT JOIN OrganizationEntity organization ON organization.id = financierEntity.identity
  LEFT JOIN UserEntity user ON user.id = financierEntity.identity
  WHERE ivf.investmentVehicle.id = :investmentVehicleId
  AND (:activationStatus IS NULL OR ivf.financier.activationStatus = :activationStatus)
  GROUP BY ivf.financier, d,
           CASE
               WHEN financierEntity.financierType = 'INDIVIDUAL' THEN CONCAT(user.firstName, ' ', user.lastName)
               WHEN financierEntity.financierType = 'COOPERATE' THEN organization.name
               ELSE NULL
           END
  
""")
    Page<FinancierWithDesignationProjection> findDistinctFinanciersWithDesignationByInvestmentVehicleIdAndStatus(
            @Param("investmentVehicleId") String investmentVehicleId,
            @Param("activationStatus") ActivationStatus activationStatus,
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
    InvestmentVehicleFinancierEntity findByFinancierIdAndInvestmentVehicleId(String financierId, String investmentVehicleFinancierId);

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
}
