package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;

public interface VerificationUseCase {
    String isIdentityVerified(String token) throws MeedlException, IdentityVerificationException;

    String isIdentityVerified(IdentityVerification identityVerification) throws MeedlException, IdentityVerificationException;

    IdentityVerification verifyIdentity(IdentityVerification smileIdVerification) throws MeedlException;
}
