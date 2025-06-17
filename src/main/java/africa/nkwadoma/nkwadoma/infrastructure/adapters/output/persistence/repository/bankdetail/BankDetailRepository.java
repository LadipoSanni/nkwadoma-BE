package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.BankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailRepository extends JpaRepository<BankDetailEntity, String> {
}
