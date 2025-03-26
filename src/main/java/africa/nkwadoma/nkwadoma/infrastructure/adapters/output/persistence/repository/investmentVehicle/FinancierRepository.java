package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FinancierRepository extends JpaRepository<FinancierEntity,String> {

    Optional<FinancierEntity> findByUserIdentity_Id(String id);

    @Query("SELECT f FROM FinancierEntity f " +
            "WHERE upper(concat(f.userIdentity.firstName, ' ', f.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "OR upper(concat(f.userIdentity.lastName, ' ', f.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%'))")
    Page<FinancierEntity> findByNameFragment( @Param("nameFragment") String nameFragment, Pageable pageRequest);

    @Query("SELECT f FROM FinancierEntity f " +
            "WHERE upper(concat(f.userIdentity.firstName, ' ', f.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "OR upper(concat(f.userIdentity.lastName, ' ', f.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%'))")
    List<FinancierEntity> findByNameFragment(@Param("nameFragment") String nameFragment);

    @Query("SELECT f FROM FinancierEntity f WHERE f.userIdentity.email = :financierEmail")
    Optional<FinancierEntity> findByEmail(String financierEmail);

    @Query("SELECT f FROM FinancierEntity f " +
            "LEFT JOIN FETCH f.userIdentity u " +
            "LEFT JOIN FETCH u.nextOfKinEntity nk " +
            "WHERE f.id = :financierId")
    Optional<FinancierEntity> findByFinancierId(String financierId);


}
