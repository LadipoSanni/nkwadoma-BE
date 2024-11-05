package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken,String> {
}
