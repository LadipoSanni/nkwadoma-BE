package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    UserEntity findByBvn(String bvn);
}
