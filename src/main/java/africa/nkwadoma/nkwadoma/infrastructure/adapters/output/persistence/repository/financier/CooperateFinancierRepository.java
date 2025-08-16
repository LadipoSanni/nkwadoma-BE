package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooperateFinancierRepository extends JpaRepository<CooperateFinancierEntity, String> {
    CooperateFinancierEntity findByFinancier_UserIdentityId(String id);
}
