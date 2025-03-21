package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CooperationRepository extends JpaRepository<CooperationEntity, String> {
    CooperationEntity findByName(String email);

    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM CooperationEntity c WHERE c.userEntity.email = :email")
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM cooperation_entity c " +
            "JOIN user_entity u ON c.user_entity_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    boolean existsByEmail(String email);

    @Query("SELECT c FROM CooperationEntity c WHERE c.userEntity.email = :email")
    CooperationEntity findByEmail(String email);
}
