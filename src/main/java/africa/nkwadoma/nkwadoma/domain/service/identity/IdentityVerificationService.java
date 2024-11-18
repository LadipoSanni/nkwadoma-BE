package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentityVerificationService implements VerificationUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityVerificationRepository identityVerificationRepository;
    private final IdentityVerificationMapper identityVerificationMapper;
    private final TokenUtils tokenUtils;

    @Override
    public String isIdentityVerified(String token) throws MeedlException, IdentityVerificationException {
        String email = tokenUtils.decodeJWTGetEmail(token);
        String id = tokenUtils.decodeJWTGetId(token);
        MeedlValidator.validateEmail(email);

        Optional<IdentityVerificationEntity> optionalVerifiedIdentity = identityVerificationRepository.findByEmailAndStatus(email, IdentityVerificationStatus.VERIFIED);
        if (optionalVerifiedIdentity.isPresent()) {
            return IDENTITY_VERIFIED.getMessage();
        }
        checkIfAboveThreshold(id);
        log.info(IDENTITY_PREVIOUSLY_VERIFIED.format(email, id));
        return IDENTITY_NOT_VERIFIED.getMessage();
    }

    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException, IdentityVerificationException {
        MeedlValidator.validateObjectInstance(identityVerification);
        identityVerification.validate();
        String id = tokenUtils.decodeJWTGetId(identityVerification.getToken());

        Optional<IdentityVerificationEntity> optionalVerifiedIdentity = identityVerificationRepository.findByBvnAndStatus(identityVerification.getBvn(), IdentityVerificationStatus.VERIFIED);
        if (optionalVerifiedIdentity.isPresent()) {
            return IDENTITY_VERIFIED.getMessage();
        }
        checkIfAboveThreshold(id);

        log.info(IDENTITY_PREVIOUSLY_VERIFIED.format(" bvn/nin ", id));
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }

    private void checkIfAboveThreshold(String id) throws IdentityVerificationException {
        Long numberOfAttempts = identityVerificationRepository.countByReferralId(id);
        if (numberOfAttempts >= 5L) {
            log.error("You have reached the maximum number of verification attempts for this referral code: {}", id);
            throw new IdentityVerificationException(String.format("You have reached the maximum number of verification attempts for this referral code: %s", id));
        }
    }
}
