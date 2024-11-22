package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;

public interface IdentityVerificationUseCase {
    String isIdentityVerified(String token) throws MeedlException, IdentityVerificationException;

    String isIdentityVerified(IdentityVerification identityVerification) throws MeedlException, IdentityVerificationException;

    IdentityVerification verifyIdentity(IdentityVerification smileIdVerification) throws MeedlException;
}
