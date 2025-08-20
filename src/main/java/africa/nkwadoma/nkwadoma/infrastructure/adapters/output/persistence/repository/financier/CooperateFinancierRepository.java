package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CooperateFinancierRepository extends JpaRepository<CooperateFinancierEntity, String> {
    CooperateFinancierEntity findByFinancier_UserIdentityId(String id);
    CooperateFinancierEntity findByFinancierId(String cooperateFinancierId);


    @Query("""
    SELECT cf 
        FROM CooperateFinancierEntity cf
        JOIN FinancierEntity f on f.id = cf.financier.id
        JOIN CooperationEntity c on c.id = cf.cooperate.id
        JOIN UserEntity u on u.id = f.userIdentity.id
            
               WHERE u.id = :id
    """)
    CooperateFinancierEntity findByUserId(@Param("id") String id);


    @Query("""
    SELECT cf 
        FROM CooperateFinancierEntity cf
        JOIN FinancierEntity f on f.id = cf.financier.id
        JOIN CooperationEntity c on c.id = cf.cooperate.id
        JOIN UserEntity u on u.id = f.userIdentity.id
            
        WHERE u.role = 'COOPERATE_FINANCIER_SUPER_ADMIN' and c.name = :name
                  
    """)
    CooperateFinancierEntity findByCooperateFinancierSuperAdminByCooperateName(@Param("name") String name);

    @Query("""
    SELECT cooperateFinancier.id as id,user.firstName as firstName, user.lastName as lastName,
           user.email as email, user.role as role, cooperateFinancier.activationStatus as status,
           user.createdAt as createdAt, concat(invitee.firstName,' ',invitee.lastName) as inviteeName

            FROM CooperateFinancierEntity cooperateFinancier
            join FinancierEntity financier on financier.id = cooperateFinancier.financier.id
            join CooperationEntity cooperation on cooperation.id = cooperateFinancier.cooperate.id
            join UserEntity user on user.id = financier.userIdentity.id
            join UserEntity invitee on user.id = user.createdBy
    where cooperation.id = :cooperationId and (:activationStatus IS NULL OR cooperateFinancier.activationStatus  = :activationStatus)
    order by createdAt desc
    """)
    Page<CooperateFinancierProjection> findAllCooperateFinancierByCooperationIdAndActivationStatus(
            @Param("cooperationId") String cooperationId,
            @Param("activationStatus") ActivationStatus activationStatus, Pageable pageRequest);
}
