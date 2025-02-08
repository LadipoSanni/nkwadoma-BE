package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public class IdentityVerificationException extends InfrastructureException {
    public IdentityVerificationException(String message) {
        super(message);
    }
}
