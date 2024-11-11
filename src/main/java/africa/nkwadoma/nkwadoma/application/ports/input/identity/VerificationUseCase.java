package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface VerificationUseCase {
    String verifyByEmailUserIdentityVerified(String token) throws MeedlException;

}
