package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

    Optional<FinancierEntity> findByUserIdentity_Id(String id);
    Optional<FinancierEntity> findByUserIdentity_Email(String email);

    @Query("""
    SELECT f FROM FinancierEntity f
    WHERE (
        upper(concat(f.userIdentity.firstName, ' ', f.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
        OR upper(concat(f.userIdentity.lastName, ' ', f.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%'))
        OR upper(f.userIdentity.email) LIKE upper(concat('%', :nameFragment, '%'))
    )
    AND (
        :investmentVehicleId IS NULL OR EXISTS (
            SELECT ivf FROM InvestmentVehicleFinancierEntity ivf
            WHERE ivf.financier = f AND ivf.investmentVehicle.id = :investmentVehicleId
        )
    )
    AND (
        :financierType IS NULL OR f.financierType = :financierType
    )
    AND (
        :activationStatus IS NULL OR f.activationStatus = :activationStatus
    )
""")
    Page<FinancierEntity> findByFinancierByNameFragmentOptionalInvestmentVehicleIdFinancierTypeActivationStatus(
            @Param("nameFragment") String nameFragment,
            @Param("investmentVehicleId") String investmentVehicleId,
            @Param("financierType") FinancierType financierType,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );



    @Query("SELECT f FROM FinancierEntity f " +
            "LEFT JOIN FETCH f.userIdentity u " +
            "LEFT JOIN FETCH u.nextOfKinEntity nk " +
            "WHERE f.id = :financierId")
    Optional<FinancierEntity> findByFinancierId(String financierId);

    @Query("SELECT f FROM FinancierEntity f JOIN f.userIdentity u ORDER BY u.createdAt DESC")
    Page<FinancierEntity> findAllOrderByUserCreatedAt(Pageable pageable);

    @Query("""
        SELECT f FROM FinancierEntity f 
        JOIN f.userIdentity u 
        WHERE (:financierType IS NULL OR f.financierType = :financierType)
        AND (:activationStatus IS NULL OR f.activationStatus = :activationStatus)
        ORDER BY u.createdAt DESC
    """)
    Page<FinancierEntity> findAllByFinancierTypeOrderByUserCreatedAt(
            @Param("financierType") FinancierType financierType,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );

}
