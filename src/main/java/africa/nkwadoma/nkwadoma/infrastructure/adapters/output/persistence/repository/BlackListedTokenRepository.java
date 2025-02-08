package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken,String> {
    @Query("SELECT b FROM BlackListedToken b WHERE b.expirationDate < CURRENT_TIMESTAMP")
    List<BlackListedToken> findExpiredTokens();
}
