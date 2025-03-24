package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CooperationRepository extends JpaRepository<CooperationEntity, String> {
    CooperationEntity findByName(String email);

    boolean existsByName(String name);

@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
        "FROM CooperationEntity c WHERE c.userIdentity.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT c FROM CooperationEntity c WHERE c.userIdentity.email = :email")
    Optional<CooperationEntity> findByEmail(String email);
}
