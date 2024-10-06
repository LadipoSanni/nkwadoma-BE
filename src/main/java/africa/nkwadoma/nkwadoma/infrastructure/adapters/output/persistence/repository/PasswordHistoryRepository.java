package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.PasswordHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistoryEntity,String> {
    List<PasswordHistoryEntity> findByMiddlUser(String id);
}
