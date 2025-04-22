package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficialOwnerRepository extends JpaRepository<BeneficialOwnerEntity, String> {
}
