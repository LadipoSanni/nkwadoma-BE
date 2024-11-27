package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;


public interface IdentityVerificationOutputPort {
    PremblyResponse verifyIdentity (IdentityVerification identityVerification) throws MeedlException;
    PremblyResponse verifyLiveliness(IdentityVerification identityVerification);
    PremblyResponse verifyBvn(IdentityVerification identityVerification) throws MeedlException;
}
