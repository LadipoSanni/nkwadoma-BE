package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;

public class QoreIdAdapter implements IdentityVerificationOutputPort {
    @Override
    public PremblyNinResponse verifyIdentity(IdentityVerification identityVerification) throws InfrastructureException {
        return null;
    }
}
