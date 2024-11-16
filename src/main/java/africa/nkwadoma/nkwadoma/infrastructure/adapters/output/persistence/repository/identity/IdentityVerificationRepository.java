package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdentityVerificationRepository extends JpaRepository<IdentityVerificationEntity, String> {
    Optional<IdentityVerificationEntity> findByEmailAndStatus(String email, IdentityVerificationStatus identityVerificationStatus);

    Optional<IdentityVerificationEntity> findByBvnAndStatus(String bvn, IdentityVerificationStatus identityVerificationStatus);

    Long countByReferralId(String id);
}
