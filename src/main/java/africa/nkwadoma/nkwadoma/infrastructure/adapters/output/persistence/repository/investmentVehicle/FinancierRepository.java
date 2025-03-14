package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

    Optional<FinancierEntity> findByUserIdentity_Id(String id);

    @Query("SELECT f FROM FinancierEntity f " +
            "WHERE upper(concat(f.userIdentity.firstName, ' ', f.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "OR upper(concat(f.userIdentity.lastName, ' ', f.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%'))")
    Page<FinancierEntity> findByNameFragment( @Param("nameFragment") String nameFragment, Pageable pageRequest);

    @Query("""
    select fe.id as id,fe.financierType as financierType,
           fe.userIdentity as individual,
           n as nextOfKin,
           oe as organizationEntity
           
    from FinancierEntity fe
    left join UserEntity ue on fe.userIdentity.id = ue.id
    left join OrganizationEntity oe on fe.organizationEntity.id = oe.id
    left join NextOfKinEntity n on ue.id = n.userEntity.id
    where fe.id = :financierId
""")
    Optional<FinancierDetailProjection> findByFinancierId(@Param("financierId") String financierId);

}
