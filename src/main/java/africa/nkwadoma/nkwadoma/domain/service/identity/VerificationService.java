package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationService {
    private final UserIdentityOutputPort userIdentityOutputPort;
    public void verifyUser(IdentityVerification identityVerification) {
        UserIdentity userIdentity = userIdentityOutputPort.findByBvnOrNin(identityVerification);
        if (userIdentity == null) {
            throw new RuntimeException("User not found");
        }
        if (userIdentity.isVerified()) {
            throw new RuntimeException("User already verified");
        }
        userIdentity.setVerified(true);
        userIdentityOutputPort.save(userIdentity);
    }
    public void isUserBvnOrNinAlreadyVerified(IdentityVerification identityVerification {
        userIdentityOutputPort.findByBvnOrNin(identityVerification);
    }
}
