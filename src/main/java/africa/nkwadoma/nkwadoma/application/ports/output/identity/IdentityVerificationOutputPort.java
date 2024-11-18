package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;


public interface IdentityVerificationOutputPort {

    PremblyNinResponse verifyIdentity(IdentityVerification identityVerification) throws InfrastructureException;

}
