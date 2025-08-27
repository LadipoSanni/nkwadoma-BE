package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.OrganizationBankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationBankDetailRepository extends JpaRepository<OrganizationBankDetailEntity, String> {
}
