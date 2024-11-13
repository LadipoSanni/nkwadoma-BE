package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.VerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.IdentityVerificationRepository;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentityVerificationService implements VerificationUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityVerificationRepository identityVerificationRepository;
    private final IdentityVerificationMapper identityVerificationMapper;
    private final TokenUtils tokenUtils;
    private final PremblyAdapter premblyAdapter;

    @Override
    public String verifyIdentity(String token) throws MeedlException {
        String email = tokenUtils.decodeJWT(token);
        MeedlValidator.validateEmail(email);
        UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
        boolean identityVerified = isIdentityVerified(foundUser);
        if (identityVerified) {
            log.info(USER_EMAIL_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
            return IDENTITY_PREVIOUSLY_VERIFIED.getMessage();
        }
        log.info(USER_EMAIL_NOT_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
        return IDENTITY_NOT_VERIFIED.getMessage();
    }
    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        boolean isPreviouslyVerified = findByBvnOrNin(identityVerification);
        if (isPreviouslyVerified) {
            log.info(USER_EMAIL_PREVIOUSLY_VERIFICATION.format(" bvn/nin ",isPreviouslyVerified));
            return IDENTITY_VERIFIED.getMessage();
        }
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }

    private boolean findByBvnOrNin(IdentityVerification identityVerification) throws MeedlException {
        identityVerification.validate();
        Optional<IdentityVerificationEntity> optionalIdentityVerificationEntity = identityVerificationRepository.findByBvn(identityVerification.getBvn());
        if (optionalIdentityVerificationEntity.isEmpty()) {
            optionalIdentityVerificationEntity = identityVerificationRepository.findByNin(identityVerification.getNin());
        }
        if (optionalIdentityVerificationEntity.isPresent()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private boolean isIdentityVerified(UserIdentity foundUser){
        return true;
    }

}
