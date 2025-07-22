package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;

public interface IdentityVerificationFailureRecordOutputPort {
    IdentityVerificationFailureRecord createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord record);

    Long countByUserId(String referralId);
}
