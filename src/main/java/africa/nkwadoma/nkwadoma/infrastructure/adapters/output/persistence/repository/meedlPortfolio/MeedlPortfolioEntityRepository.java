package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlPortfolio;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.MeedlPortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeedlPortfolioEntityRepository extends JpaRepository<MeedlPortfolioEntity, String> {
    MeedlPortfolioEntity findByPortfolioName(String meedl);
}
