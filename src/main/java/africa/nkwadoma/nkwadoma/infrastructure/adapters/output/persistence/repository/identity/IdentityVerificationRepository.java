package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityVerificationRepository extends JpaRepository<IdentityVerificationEntity, String> {
    IdentityVerificationEntity findByBvn(String bvn);
}
