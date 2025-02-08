package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;

public class QoreIdAdapter implements IdentityVerificationOutputPort {
    @Override
    public PremblyResponse verifyIdentity(IdentityVerification identityVerification) throws IdentityVerificationException {
        return null;
    }

    @Override
    public PremblyResponse verifyLiveliness(IdentityVerification identityVerification) {
        return null;
    }

    @Override
    public PremblyResponse verifyBvn(IdentityVerification identityVerification) throws MeedlException {
        return null;
    }

    @Override
    public PremblyBvnResponse verifyBvnLikeness(IdentityVerification identityVerification) throws MeedlException {
        return null;
    }

    @Override
    public PremblyResponse verifyNin(IdentityVerification identityVerification) throws IdentityVerificationException {
        return null;
    }

    @Override
    public PremblyNinResponse verifyNinLikeness(IdentityVerification identityVerification) throws MeedlException {
        return null;
    }
}
