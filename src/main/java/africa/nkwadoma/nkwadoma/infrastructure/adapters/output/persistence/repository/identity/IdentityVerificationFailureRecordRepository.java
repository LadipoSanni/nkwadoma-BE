package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationFailureRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityVerificationFailureRecordRepository extends JpaRepository<IdentityVerificationFailureRecordEntity, String> {
    Long countByUserId(String referralId);
}
