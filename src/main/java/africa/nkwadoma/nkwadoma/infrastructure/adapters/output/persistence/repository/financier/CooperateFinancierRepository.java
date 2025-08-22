package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CooperateFinancierRepository extends JpaRepository<CooperateFinancierEntity, String> {
    CooperateFinancierEntity findByFinancier_Identity(String id);
    CooperateFinancierEntity findByFinancierId(String cooperateFinancierId);


    @Query("""
    SELECT cf 
        FROM CooperateFinancierEntity cf
        JOIN FinancierEntity f on f.id = cf.financier.id
        JOIN CooperationEntity c on c.id = cf.cooperate.id
        JOIN UserEntity u on u.id = f.identity
            
               WHERE u.id = :id
    """)
    CooperateFinancierEntity findByUserId(@Param("id") String id);


    @Query("""
    SELECT cf 
        FROM CooperateFinancierEntity cf
        JOIN FinancierEntity f on f.id = cf.financier.id
        JOIN CooperationEntity c on c.id = cf.cooperate.id
        JOIN UserEntity u on u.id = f.identity
            
        WHERE u.role = 'COOPERATE_FINANCIER_SUPER_ADMIN' and c.name = :name
                  
    """)
    CooperateFinancierEntity findByCooperateFinancierSuperAdminByCooperateName(@Param("name") String name);

    @Query("""
    SELECT cooperateFinancier.id as id,user.firstName as firstName, user.lastName as lastName,
           user.email as email, user.role as role, cooperateFinancier.activationStatus as status,
           user.createdAt as createdAt,
           COALESCE(concat(invitee.firstName, ' ', invitee.lastName), 'N/A') as inviteeName

            FROM CooperateFinancierEntity cooperateFinancier
             join FinancierEntity financier on financier.id = cooperateFinancier.financier.id
             join CooperationEntity cooperation on cooperation.id = cooperateFinancier.cooperate.id
             join UserEntity user on user.id = financier.identity
             join UserEntity invitee on invitee.id = user.createdBy
    where cooperation.id = :cooperationId and (:activationStatus IS NULL OR cooperateFinancier.activationStatus  = :activationStatus)
        and user.role != 'COOPERATE_FINANCIER_SUPER_ADMIN'
    order by user.createdAt desc
    """)
    Page<CooperateFinancierProjection> findAllCooperateFinancierByCooperationIdAndActivationStatus(
            @Param("cooperationId") String cooperationId,
            @Param("activationStatus") ActivationStatus activationStatus, Pageable pageRequest);

    @Query("""
    SELECT cooperateFinancier.id as id,user.firstName as firstName, user.lastName as lastName,
           user.email as email, user.role as role, cooperateFinancier.activationStatus as status,
           user.createdAt as createdAt,
           COALESCE(concat(invitee.firstName, ' ', invitee.lastName), 'N/A') as inviteeName

            FROM CooperateFinancierEntity cooperateFinancier
             join FinancierEntity financier on financier.id = cooperateFinancier.financier.id
             join CooperationEntity cooperation on cooperation.id = cooperateFinancier.cooperate.id
             join UserEntity user on user.id = financier.identity
             join UserEntity invitee on invitee.id = user.createdBy
    where (
        lower(user.firstName) like lower(concat('%', :name, '%'))
        or lower(user.lastName) like lower(concat('%', :name, '%'))
    ) and
         cooperation.id = :cooperationId and (:activationStatus IS NULL OR cooperateFinancier.activationStatus  = :activationStatus)
        and user.role != 'COOPERATE_FINANCIER_SUPER_ADMIN'
    order by user.createdAt desc
    """)
    Page<CooperateFinancierProjection> findAllByStaffNameAndCooperationIdAndActivationStatus(
            @Param("cooperationId") String cooperationId,
            @Param("activationStatus") ActivationStatus activationStatus,
            @Param("name") String name, Pageable pageRequest
    );
}
