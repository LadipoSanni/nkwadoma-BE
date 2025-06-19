
package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;

public interface IdentityVerificationUseCase {
    String verifyIdentity(String loanReferralId) throws MeedlException;
    String verifyIdentity(IdentityVerification identityVerification) throws MeedlException;
    String createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord verificationFailureRecord) throws IdentityVerificationException, IdentityException;
}
