package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CooperateFinancierRepository extends JpaRepository<CooperateFinancierEntity, String> {


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
}
