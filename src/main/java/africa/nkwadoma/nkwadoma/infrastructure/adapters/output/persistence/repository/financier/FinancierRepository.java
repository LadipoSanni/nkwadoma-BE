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
            join UserEntity user on user.id = financier.identity
         where financier.identity  = :id
        
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
    SELECT 
        f.id AS id, 
        CASE 
            WHEN f.financierType = 'INDIVIDUAL' THEN CONCAT(user.firstName, ' ', user.lastName)
            WHEN f.financierType = 'COOPERATE' THEN organization.name
        END AS name,
        f.financierType AS financierType,
        f.activationStatus AS activationStatus,
        f.totalAmountInvested AS amountInvested,
       
        CASE 
            WHEN f.financierType = 'INDIVIDUAL' THEN CONCAT(inviteeUser.firstName, ' ', inviteeUser.lastName)
            WHEN f.financierType = 'COOPERATE' THEN CONCAT(inviteeOrg.firstName, ' ', inviteeOrg.lastName)
        END AS inviteeName
    FROM FinancierEntity f
    LEFT JOIN UserEntity user 
        ON f.financierType = 'INDIVIDUAL' AND user.id = f.identity
    LEFT JOIN OrganizationEntity organization 
        ON f.financierType = 'COOPERATE' AND organization.id = f.identity
    LEFT JOIN UserEntity inviteeUser 
        ON f.financierType = 'INDIVIDUAL' AND inviteeUser.id = user.createdBy
    LEFT JOIN UserEntity inviteeOrg 
        ON f.financierType = 'COOPERATE' AND inviteeOrg.id = organization.createdBy
    WHERE (
        :nameFragment IS NULL OR (
            (f.financierType = 'INDIVIDUAL' AND (
                UPPER(CONCAT(user.firstName, ' ', user.lastName)) LIKE UPPER(CONCAT('%', :nameFragment, '%'))
                OR UPPER(CONCAT(user.lastName, ' ', user.firstName)) LIKE UPPER(CONCAT('%', :nameFragment, '%'))
                OR UPPER(user.email) LIKE UPPER(CONCAT('%', :nameFragment, '%'))
            ))
            OR
            (f.financierType = 'COOPERATE' AND (
                UPPER(organization.name) LIKE UPPER(CONCAT('%', :nameFragment, '%'))
                OR UPPER(organization.email) LIKE UPPER(CONCAT('%', :nameFragment, '%'))
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
    ORDER BY f.createdAt DESC
""")
    Page<FinancierProjection> findByFinancierByNameFragmentOptionalInvestmentVehicleIdFinancierTypeActivationStatus(
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
    SELECT 
        f.id AS id, 
        CASE 
            WHEN f.financierType = 'INDIVIDUAL' THEN CONCAT(user.firstName, ' ', user.lastName)
            WHEN f.financierType = 'COOPERATE' THEN organization.name
        END AS name,
        f.financierType AS financierType,
        f.activationStatus AS activationStatus,
        f.totalAmountInvested AS amountInvested,
        CASE 
            WHEN f.financierType = 'INDIVIDUAL' THEN CONCAT(inviteeUser.firstName, ' ', inviteeUser.lastName)
            WHEN f.financierType = 'COOPERATE' THEN CONCAT(inviteeOrg.firstName, ' ', inviteeOrg.lastName)
        END AS invitedBy
    FROM FinancierEntity f
    LEFT JOIN UserEntity user 
        ON f.financierType = 'INDIVIDUAL' AND user.id = f.identity
    LEFT JOIN OrganizationEntity organization 
        ON f.financierType = 'COOPERATE' AND organization.id = f.identity 
    LEFT JOIN UserEntity inviteeUser 
        ON f.financierType = 'INDIVIDUAL' AND inviteeUser.id = user.createdBy
    LEFT JOIN UserEntity inviteeOrg 
        ON f.financierType = 'COOPERATE' AND inviteeOrg.id = organization.createdBy
    WHERE (:financierType IS NULL OR f.financierType = :financierType)
    AND (:activationStatus IS NULL OR f.activationStatus = :activationStatus)
    ORDER BY f.createdAt DESC
""")
    Page<FinancierProjection> findAllByFinancierTypeOrderByUserCreatedAt(
            @Param("financierType") FinancierType financierType,
            @Param("activationStatus") ActivationStatus activationStatus,
            Pageable pageable
    );

    FinancierEntity findByIdentity(String id);

    @Query("""
    SELECT f FROM FinancierEntity f
        JOIN OrganizationEntity  organization on  organization.id = f.identity
        JOIN OrganizationEmployeeEntity  organizationEmployee on organizationEmployee.organization = organization.id
        JOIN UserEntity user on user.id = organizationEmployee.meedlUser.id
         where user.id = :id    
    """)
    FinancierEntity findByCooperateStaffUserId(@Param("id") String id);
}
