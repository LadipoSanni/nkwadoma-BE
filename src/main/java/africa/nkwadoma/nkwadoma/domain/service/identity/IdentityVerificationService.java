package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.VerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentityVerificationService implements VerificationUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final TokenUtils tokenUtils;

    @Override
    public String verifyByEmailUserIdentityVerified(String token) throws MeedlException {
        String email = tokenUtils.decodeJWT(token);
        MeedlValidator.validateEmail(email);
        UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
        boolean identityVerified = isIdentityVerified(foundUser);
        if (identityVerified) {
            log.info(USER_EMAIL_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
            return IDENTITY_VERIFIED.getMessage();
        }
        log.info(USER_EMAIL_NOT_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
        return IDENTITY_NOT_VERIFIED.getMessage();
    }
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
    private boolean isIdentityVerified(UserIdentity foundUser){
        return true;
    }

}
