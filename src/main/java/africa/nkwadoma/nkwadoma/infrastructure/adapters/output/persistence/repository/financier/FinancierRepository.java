package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

    @Query("""
    select financier 
        from FinancierEntity financier
        join UserEntity  user on user.id = financier.identity 
         where user.id = :id  
        
    """)
    Optional<FinancierEntity> findByUserIdentity_Id(@Param("id") String id);

    @Query("""
    select financier 
        from FinancierEntity financier
        join UserEntity  user on user.id = financier.identity 
         where user.email = :email  
        
    """)
    Optional<FinancierEntity> findByUserIdentity_Email(@Param("email") String email);

    @Query("""
    SELECT f
    FROM FinancierEntity f
    JOIN UserEntity user 
        ON f.financierType = 'INDIVIDUAL' AND user.id = f.identity
    JOIN OrganizationEntity organization 
        ON f.financierType = 'COOPERATE' AND organization.id = f.identity
    WHERE (
        :nameFragment IS NULL OR (
            (f.financierType = 'INDIVIDUAL' AND (
                upper(concat(user.firstName, ' ', user.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
                OR upper(concat(user.lastName, ' ', user.firstName)) LIKE upper(concat('%', :nameFragment, '%'))
                OR upper(user.email) LIKE upper(concat('%', :nameFragment, '%'))
            ))
            OR
            (f.financierType = 'COOPERATE' AND (
                upper(organization.name) LIKE upper(concat('%', :nameFragment, '%'))
                OR upper(organization.email) LIKE upper(concat('%', :nameFragment, '%'))
            ))
        )
    )
    AND (
        :investmentVehicleId IS NULL OR EXISTS (
            SELECT 1 
            FROM InvestmentVehicleFinancierEntity ivf
            WHERE ivf.financier = f 
              AND ivf.investmentVehicle.id = :investmentVehicleId
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
            "WHERE f.id = :financierId")
    Optional<FinancierEntity> findByFinancierId(String financierId);


    @Query("""
        SELECT f FROM FinancierEntity f 
        LEFT JOIN UserEntity user on user.id = f.identity
        LEFT JOIN OrganizationEntity organization on organization.id = f.identity    
        WHERE (:financierType IS NULL OR f.financierType = :financierType)
        AND (:activationStatus IS NULL OR f.activationStatus = :activationStatus)
        ORDER BY user.createdAt DESC
    """)
    Page<FinancierEntity> findAllByFinancierTypeOrderByUserCreatedAt(
            @Param("financierType") FinancierType financierType,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );

}
