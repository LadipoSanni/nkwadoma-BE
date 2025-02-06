package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;


public interface IdentityVerificationOutputPort {
    PremblyResponse verifyIdentity (IdentityVerification identityVerification) throws MeedlException;
    PremblyResponse verifyLiveliness(IdentityVerification identityVerification);
    PremblyResponse verifyBvn(IdentityVerification identityVerification) throws MeedlException;
    PremblyBvnResponse verifyBvnLikeness(IdentityVerification identityVerification) throws MeedlException;
    PremblyResponse verifyNin(IdentityVerification identityVerification) throws MeedlException;
    PremblyResponse verifyNinLikeness(IdentityVerification identityVerification) throws MeedlException;

}
