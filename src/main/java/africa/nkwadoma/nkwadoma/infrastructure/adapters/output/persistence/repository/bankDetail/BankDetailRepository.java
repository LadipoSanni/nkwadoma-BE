package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankDetail;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail.BankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailRepository extends JpaRepository<BankDetailEntity, String> {
}
