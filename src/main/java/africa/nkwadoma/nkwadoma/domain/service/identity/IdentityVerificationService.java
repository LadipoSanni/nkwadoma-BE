package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
//    private final IdentityVerificationRepository identityVerificationRepository;
    private final IdentityVerificationMapper identityVerificationMapper;
    @Qualifier("premblyAdapter")
    private final IdentityVerificationOutputPort identityVerificationOutputPort;
    private final TokenUtils tokenUtils;
    private final Emai


    @Override
    public String verifyIdentity(String token) throws MeedlException {
        String email = tokenUtils.decodeJWTGetEmail(token);
        String loanReferralId = tokenUtils.decodeJWTGetId(token);
        MeedlValidator.validateEmail(email);
        checkIfAboveThreshold(loanReferralId);
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(email);
        if (!userIdentity.isIdentityVerified()) {
            addedToLoaneeLoan(loanReferralId);
            log.info("Identity: Email {}. Loan referral id {}. Verified ", email, loanReferralId);
            return IDENTITY_VERIFIED.getMessage();
        } else {
            log.info("Identity: Email {}. Loan referral id {}, not verified ", email, loanReferralId);
            return IDENTITY_NOT_VERIFIED.getMessage();
        }
    }

    private void addedToLoaneeLoan(String loanReferralId) {

    }

    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification);
        String bvn = tokenUtils.decodeJWTGetId(identityVerification.getToken());
        checkIfAboveThreshold(identityVerification.getLoanReferralId());
        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(bvn);
        if (!userIdentity.isIdentityVerified()){
            try{
             identityVerificationOutputPort.verifyBvn(identityVerification);
            }catch (MeedlException exception) {
                IdentityVerificationFailureRecord identityVerificationFailureRecord = new IdentityVerificationFailureRecord();
                identityVerificationFailureRecord.setEmail(userIdentity.getEmail());
                identityVerificationFailureRecord.setReferralId(identityVerification.getLoanReferralId());
                identityVerificationFailureRecord.setServiceProvider(ServiceProvider.PREMBLY);
                identityVerificationFailureRecord.setReason(exception.getMessage());
                createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
                //notify inviter
            }}
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }


//    @Override
//    public IdentityVerification verifyIdentity(IdentityVerification smileIdVerification) throws MeedlException {
//        MeedlValidator.validateObjectInstance(smileIdVerification);
//        IdentityVerificationEntity identityVerificationEntity = identityVerificationMapper.mapToIdentityVerificationEntity(smileIdVerification);
//        identityVerificationEntity.setStatus(IdentityVerificationStatus.VERIFIED);
////        identityVerificationEntity = identityVerificationRepository.save(identityVerificationEntity);
//        return identityVerificationMapper.mapToIdentityVerification(identityVerificationEntity);
//    }

    private void checkIfAboveThreshold(String loanReferralId) throws IdentityVerificationException {
        Long numberOfAttempts = identityVerificationFailureRecordOutputPort.countByReferralId(loanReferralId);
        if (numberOfAttempts >= 5L){
            log.error("You have reached the maximum number of verification attempts for this referral code: {}", loanReferralId);
            throw new IdentityVerificationException(String.format("You have reached the maximum number of verification attempts for this referral code: %s", loanReferralId));
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
