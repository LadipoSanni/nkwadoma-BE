package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;


public interface IdentityVerificationOutputPort {


    PremblyNinResponse verifyIdentity (IdentityVerification identityVerification) throws InfrastructureException;
}
