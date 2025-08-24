package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.EntityBankDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityBankDetailRepository extends JpaRepository<EntityBankDetail, String> {
}
