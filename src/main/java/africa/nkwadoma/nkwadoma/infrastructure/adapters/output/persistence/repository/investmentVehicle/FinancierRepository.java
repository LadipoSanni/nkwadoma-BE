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
}
