package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancierBeneficialOwnerRepository extends JpaRepository<FinancierBeneficialOwnerEntity, String> {
}
