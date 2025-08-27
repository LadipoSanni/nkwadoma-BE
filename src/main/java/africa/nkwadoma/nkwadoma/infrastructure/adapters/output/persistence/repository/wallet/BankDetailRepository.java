package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.BankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailRepository extends JpaRepository<BankDetailEntity, String> {
}
