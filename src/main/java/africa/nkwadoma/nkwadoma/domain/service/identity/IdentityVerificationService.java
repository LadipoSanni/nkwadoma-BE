package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
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
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.Verification;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;

@Slf4j
@Service
public class IdentityVerificationService implements IdentityVerificationUseCase {
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Autowired
    @Qualifier("premblyAdapter")
    private IdentityVerificationOutputPort identityVerificationOutputPort;

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
            log.info("Verified: Loan referral id {}. Verified ", loanReferralId);
            return IDENTITY_VERIFIED.getMessage();
        } else {
            log.info("Not verified: Loan referral id {}, not verified ", loanReferralId);
            return IDENTITY_NOT_VERIFIED.getMessage();
        }
    }

    private void addedToLoaneeLoan(String loanReferralId) {
        log.info("Added to Loanee loan to loanee's list of loans {} ", loanReferralId);
    }
    public PremblyResponse createTestPremblyResponse(){
        PremblyResponse response = new PremblyBvnResponse();
        Verification verifier = Verification.builder().status("VERIFIED").build();
        response.setDetail("VERIFIED");
        response.setVerification(verifier);
        response.setResponseCode("CREATED");
        return response;
    }

    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification);
        log.info("Verifying identity : {}", identityVerification);
        String bvn = identityVerification.getBvn();
        LoanReferral loanReferral = loanReferralOutputPort.findById(identityVerification.getLoanReferralId());
        log.info("Loan referral {}", loanReferral);
//        UserIdentity userIdentity = userIdentityOutputPort.findById(loanReferral.getLoanee().getUserIdentity().getId())
        checkIfAboveThreshold(identityVerification.getLoanReferralId());
        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(bvn);
        if (ObjectUtils.isEmpty(userIdentity) || !userIdentity.isIdentityVerified()){
            try{
//                PremblyResponse premblyResponse = identityVerificationOutputPort.verifyBvn(identityVerification);
                PremblyResponse premblyResponse = createTestPremblyResponse();
                if (premblyResponse.getVerification() != null &&
                    premblyResponse.getVerification().getStatus() != null &&
                    premblyResponse.getVerification().getStatus().equals("VERIFIED")){
                    updateLoaneeDetail(identityVerification, loanReferral);
                    addedToLoaneeLoan(identityVerification.getLoanReferralId());
                    log.info("Identity is verified: Loan referral id {}. Verified ", identityVerification.getLoanReferralId());
                    return IDENTITY_VERIFIED.getMessage();
                }else {
                    log.info("Identity: Loan referral id {}. Not verified ", identityVerification.getLoanReferralId());
                    createVerificationFailure(loanReferral, premblyResponse.getDetail(), ServiceProvider.PREMBLY);
                    return IDENTITY_NOT_VERIFIED.getMessage();
                }
            }catch (MeedlException exception) {
                log.error("Error verifying users identity... {}", exception.getMessage());
                createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
                //notify inviter
            }}else{
                addedToLoaneeLoan(identityVerification.getLoanReferralId());
                return IDENTITY_VERIFIED.getMessage();
        }
        return IDENTITY_VERIFICATION_PROCESSING.getMessage();
    }

    private void updateLoaneeDetail(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
        UserIdentity userIdentity;
        userIdentity = loanReferral.getLoanee().getUserIdentity();
        userIdentity.setIdentityVerified(Boolean.TRUE);
        userIdentity.setBvn(identityVerification.getBvn());
        userIdentity.setNin(identityVerification.getNin());
        userIdentityOutputPort.save(userIdentity);
    }

    private void createVerificationFailure(LoanReferral loanReferral, String message, ServiceProvider serviceProvider) throws MeedlException {
        IdentityVerificationFailureRecord identityVerificationFailureRecord = new IdentityVerificationFailureRecord();
        identityVerificationFailureRecord.setEmail(loanReferral.getLoanee().getUserIdentity().getEmail());
        identityVerificationFailureRecord.setReferralId(loanReferral.getId());
        identityVerificationFailureRecord.setServiceProvider(serviceProvider);
        identityVerificationFailureRecord.setReason(message);
        createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
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
            log.error("Number of failure verification exceeded for {}", identityVerificationFailureRecord.getReferralId());
            throw new IdentityVerificationException(BLACKLISTED_REFERRAL.getMessage());
        }
        log.info("Verification failure saved successfully for {}", identityVerificationFailureRecord.getReferralId());
        return IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage();
    }
}
