package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlportfolio;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioEntityRepository extends JpaRepository<PortfolioEntity, String> {
    PortfolioEntity findByPortfolioName(String name);
}
