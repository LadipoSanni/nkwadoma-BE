package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.IdentityVerificationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PremblyAdapter implements IdentityVerificationOutputPort {



    @Value("${PREMBLY_URL}")
    private String premblyUrl;


    @Value("${PREMBLY_APP_ID}")
    private String appId;

    @Value("${PREMBLY_APP_KEY}")
    private String apiKey;

    @Override
    public IdentityVerificationResponse verifyIdentity(IdentityVerification identityVerification) {

        return null;
    }

    private  void  validateIdentityVerificationRequest(IdentityVerification identityVerification) throws InfrastructureException {
        if (identityVerification ==  null ||
                StringUtils.isEmpty(identityVerification.getNumber()) &&
                        StringUtils.isEmpty(identityVerification.getNumber())) throw  new InfrastructureException("credentials should not be empty");
    }

}
