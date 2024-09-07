package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagementOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdapter implements IdentityManagementOutputPort {


    @Override
    public UserIdentity createUser(UserIdentity userIdentity) {
        return null;
    }
}
