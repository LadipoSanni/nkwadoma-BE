package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.IdentityVerificationRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
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
public class IdentityVerificationService implements IdentityVerificationUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
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
    public String isIdentityVerified(IdentityVerification identityVerification) throws MeedlException, IdentityVerificationException {
        MeedlValidator.validateObjectInstance(identityVerification);
        identityVerification.validate();
        String id = tokenUtils.decodeJWTGetId(identityVerification.getToken());

        Optional<IdentityVerificationEntity> optionalVerifiedIdentity = identityVerificationRepository.findByBvnAndStatus(identityVerification.getBvn(), IdentityVerificationStatus.VERIFIED);
        if (optionalVerifiedIdentity.isPresent()) {
            return IDENTITY_VERIFIED.getMessage();
        }
        checkIfAboveThreshold(id);

        log.info(IDENTITY_PREVIOUSLY_VERIFIED.format(" bvn/nin ",id));
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }
    @Override
    public IdentityVerification verifyIdentity(IdentityVerification smileIdVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(smileIdVerification);
        IdentityVerificationEntity identityVerificationEntity = identityVerificationMapper.mapToIdentityVerificationEntity(smileIdVerification);
        identityVerificationEntity.setStatus(IdentityVerificationStatus.VERIFIED);
        identityVerificationEntity = identityVerificationRepository.save(identityVerificationEntity);
        return identityVerificationMapper.mapToIdentityVerification(identityVerificationEntity);
    }
    private void checkIfAboveThreshold(String id) throws IdentityVerificationException {
        Long numberOfAttempts = identityVerificationRepository.countByReferralId(id);
        if (numberOfAttempts >= 5L){
            log.error("You have reached the maximum number of verification attempts for this referral code: {}", id);
            throw new IdentityVerificationException(String.format("You have reached the maximum number of verification attempts for this referral code: %s", id));
        }
    }

    @Override
    public String createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord identityVerificationFailureRecord) throws IdentityVerificationException {
        identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
        Long numberOfFailedVerifications = identityVerificationFailureRecordOutputPort.countByReferralId(identityVerificationFailureRecord.getReferralId());
        if (numberOfFailedVerifications >= 5){
            throw new IdentityVerificationException(BLACKLISTED_REFERRAL.getMessage());
        }
        return IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage();
    }
}
