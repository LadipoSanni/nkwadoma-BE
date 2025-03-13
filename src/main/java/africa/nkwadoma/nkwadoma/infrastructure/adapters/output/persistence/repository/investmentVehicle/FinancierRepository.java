package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

    Optional<FinancierEntity> findByIndividual_Id(String id);

    @Query("SELECT f FROM FinancierEntity f " +
            "WHERE upper(concat(f.individual.firstName, ' ', f.individual.lastName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "OR upper(concat(f.individual.lastName, ' ', f.individual.firstName)) LIKE upper(concat('%', :nameFragment, '%'))")
    List<FinancierEntity> findByNameFragment( @Param("nameFragment") String nameFragment);

    @Query("""
    select fe.id as id,fe.financierType as financierType,
           fe.individual as individual,
           n as nextOfKin,
           oe as organizationEntity
           
    from FinancierEntity fe
    join UserEntity ue on fe.individual.id = ue.id
    join OrganizationEntity oe on fe.organizationEntity.id = oe.id
    left join NextOfKinEntity n on ue.id = n.userEntity.id
    where fe.id = :id
""")
    Optional<FinancierDetailProjection> findByFinancierId(@Param("financierId") String financierId);

}
