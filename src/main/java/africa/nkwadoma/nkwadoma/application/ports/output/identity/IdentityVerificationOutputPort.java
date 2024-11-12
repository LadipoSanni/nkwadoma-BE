package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.NinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;


public interface IdentityVerificationOutputPort {

    NinResponse verifyIdentity (IdentityVerification identityVerification) throws InfrastructureException;

}
