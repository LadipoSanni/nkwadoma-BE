package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;

public interface VerificationUseCase {
    String isIdentityVerified(String token) throws MeedlException;

    String verifyIdentity(IdentityVerification identityVerification) throws MeedlException;

    String createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord verificationFailureRecord);
}
