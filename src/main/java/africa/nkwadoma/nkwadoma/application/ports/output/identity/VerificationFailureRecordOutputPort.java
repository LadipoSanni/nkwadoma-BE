package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;

public interface VerificationFailureRecordOutputPort {
    IdentityVerificationFailureRecord createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord record);

    Long countByReferralId(String referralId);
}
