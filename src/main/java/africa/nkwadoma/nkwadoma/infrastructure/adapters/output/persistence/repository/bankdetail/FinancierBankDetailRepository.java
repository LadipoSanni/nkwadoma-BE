package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.FinancierBankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancierBankDetailRepository extends JpaRepository<FinancierBankDetailEntity, String> {
}
