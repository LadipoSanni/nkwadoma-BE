package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.ViewLoanReferralsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.LoanReferralRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;

@Slf4j
@Service
public class IdentityVerificationService implements IdentityVerificationUseCase {
    @Autowired
    private LoanReferralRestMapper loanReferralRestMapper;
    @Autowired
    private ViewLoanReferralsUseCase viewLoanReferralsUseCase;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Autowired
    @Qualifier("premblyAdapter")
    private IdentityVerificationOutputPort identityVerificationOutputPort;
    @Autowired
    private TokenUtils tokenUtils;


    @Override
    public String verifyIdentity(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId);
        checkIfAboveThreshold(loanReferralId);
        LoanReferral loanReferral = loanReferralOutputPort.findLoanReferralById(loanReferralId)
                                    .orElseThrow(()-> new ResourceNotFoundException("Could not find loan referral"));
        if (ObjectUtils.isEmpty(loanReferral.getLoanee())){
            throw new MeedlException("Loan referral has no loanee assigned to it.");
        }
        UserIdentity userIdentity = loanReferral.getLoanee().getUserIdentity();
        if (userIdentity.isIdentityVerified()) {
            addedToLoaneeLoan(loanReferralId);
            log.info("Identity: Loan referral id {}. Verified ", loanReferralId);
            return IDENTITY_VERIFIED.getMessage();
        } else {
            log.info("Loan referral id {}, not verified ", loanReferralId);
            return IDENTITY_NOT_VERIFIED.getMessage();
        }
    }

    private void addedToLoaneeLoan(String loanReferralId) {

    }

    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification);
        log.info("Verifying identity : {}", identityVerification);
        String bvn = identityVerification.getBvn();
        checkIfAboveThreshold(identityVerification.getLoanReferralId());
        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(bvn);
        if (ObjectUtils.isEmpty(userIdentity) || !userIdentity.isIdentityVerified()){
            try{
                identityVerificationOutputPort.verifyBvn(identityVerification);
            }catch (MeedlException exception) {
//                ZonedDateTime zonedDateTime = ZonedDateTime
//                LoanReferral loanReferral = loanReferralRestMapper.toLoanReferral();
//                loanReferral = viewLoanReferralsUseCase.viewLoanReferral(loanReferral);
//                IdentityVerificationFailureRecord identityVerificationFailureRecord = new IdentityVerificationFailureRecord();
//                identityVerificationFailureRecord.setEmail(loanReferral.getLoanee().getUserIdentity().getEmail());
//                identityVerificationFailureRecord.setReferralId(identityVerification.getLoanReferralId());
//                identityVerificationFailureRecord.setServiceProvider(ServiceProvider.PREMBLY);
//                identityVerificationFailureRecord.setReason(exception.getMessage());
//                createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
                //notify inviter
            }}
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }


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
