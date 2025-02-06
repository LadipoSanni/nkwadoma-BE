package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.commons.IdentityVerificationMessage;
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
    private IdentityVerificationMapper identityVerificationMapper;
    @Autowired
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Autowired
    @Qualifier("premblyAdapter")
    private IdentityVerificationOutputPort identityVerificationOutputPort;
    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public String verifyIdentity(String loanReferralId) throws MeedlException, IdentityVerificationException {
        MeedlValidator.validateUUID(loanReferralId, "Please provide a valid loan referral identification.");
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
    @Override
    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        validateIdentityVerification(identityVerification);
        decryptEncryptedIdentification(identityVerification);

        LoanReferral loanReferral = fetchLoanReferral(identityVerification.getLoanReferralId());
        checkIfAboveThreshold(loanReferral.getId());

        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn());


        if (isVerificationRequired(userIdentity)){
            return processNewVerification(identityVerification, loanReferral);
        }else{
            processAnotherVerification(identityVerification, loanReferral);
            log.info("Verification previously done and was successful");
                addedToLoaneeLoan(loanReferral.getId());
                return IDENTITY_VERIFIED.getMessage();
        }
    }


    private void updateLoaneeDetail(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyNinResponse premblyResponse) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanReferral.getLoanee().getUserIdentity().getId());
        log.info("UserIdentity before update :  {}", userIdentity);
        UserIdentity updatedUserIdentity = identityVerificationMapper.updateUserIdentity(premblyResponse.getNinData(), userIdentity);
        userIdentity.setIdentityVerified(Boolean.TRUE);
        userIdentity.setBvn(identityVerification.getEncryptedBvn());
        userIdentity.setNin(identityVerification.getEncryptedNin());
        userIdentity.setImage(null);
        log.info("update user identity from prembly {}", updatedUserIdentity);
        userIdentity = userIdentityOutputPort.save(userIdentity);
        log.info("User identity details updated for loanee with user id : {}", userIdentity);
    }

    private void createVerificationFailure(LoanReferral loanReferral, String message, ServiceProvider serviceProvider) throws MeedlException, IdentityVerificationException {
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
    private void validateIdentityVerification(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification);
        MeedlValidator.validateDataElement(identityVerification.getEncryptedBvn(), IdentityVerificationMessage.INVALID_BVN.getMessage());
        MeedlValidator.validateDataElement(identityVerification.getEncryptedNin(), IdentityVerificationMessage.INVALID_NIN.getMessage());
        MeedlValidator.validateUUID(identityVerification.getLoanReferralId(), LoanMessages.INVALID_LOAN_REFERRAL_ID.getMessage());
        log.info("Verifying user identity. Loan referral id: {}", identityVerification.getLoanReferralId());
    }
    private String decrypt(String encryptedData) throws MeedlException {
        log.info("decrypting identity verification values.");
        return tokenUtils.decryptAES(encryptedData);
    }
    private LoanReferral fetchLoanReferral(String loanReferralId) throws MeedlException {
        LoanReferral loanReferral = loanReferralOutputPort.findById(loanReferralId);
        log.info("User referred : {}", loanReferral.getLoanee().getUserIdentity().getId());
        return loanReferral;
    }
    private boolean isVerificationRequired(UserIdentity userIdentity) {
        log.info("Checking if user was found by bvn or has previously done verification.");
        return ObjectUtils.isEmpty(userIdentity) || !userIdentity.isIdentityVerified();
    }
    private String processNewVerification(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
        try {
            //bvn first
            //likness nin
            //save data got frm nin to user
            PremblyResponse premblyResponse =
                    identityVerificationOutputPort.verifyBvn(identityVerification);

            PremblyNinResponse premblyNinResponse =
                    identityVerificationOutputPort.verifyNinLikeness(identityVerification);
            log.info("prembly bvn response: {}", premblyResponse);

            if (isIdentityVerified(premblyResponse)) {
                return handleSuccessfulVerification(identityVerification, loanReferral, premblyNinResponse);
            } else {
                return handleFailedVerification(loanReferral, premblyNinResponse);
            }
        } catch (MeedlException exception) {
            log.error("Error verifying user's identity... {}", exception.getMessage());
            createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
            throw new MeedlException(exception.getMessage());
        }
    }

    private String processAnotherVerification(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
        try{
            PremblyResponse premblyResponse =
                    identityVerificationOutputPort.verifyBvn(identityVerification);
                    PremblyNinResponse premblyNinResponse =
                            identityVerificationOutputPort.verifyNinLikeness(identityVerification);
            log.info("prembly bvn response: {}", premblyNinResponse);

            if (premblyNinResponse.getFaceData().getMessage().equals("Fatch Match") ||
                    !premblyResponse.getVerification().isValidIdentity()){
                return handleSuccessfulVerification(identityVerification,loanReferral,premblyNinResponse);
            }else {
                return handleFailedVerification(loanReferral,premblyNinResponse);
            }
        }catch (MeedlException exception){
            log.error("Error verifying user's identity... {}", exception.getMessage());
            createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
            throw new MeedlException(exception.getMessage());
        }
    }

    private boolean isIdentityVerified(PremblyResponse premblyResponse) {
        return premblyResponse.getVerification() != null &&
                "VERIFIED".equals(premblyResponse.getVerification().getStatus());
    }

    private String handleSuccessfulVerification(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyNinResponse premblyResponse) throws MeedlException {
        updateLoaneeDetail(identityVerification, loanReferral, premblyResponse);
        addedToLoaneeLoan(identityVerification.getLoanReferralId());
        log.info("Identity is verified: Loan referral id {}. Verified", identityVerification.getLoanReferralId());
        return IDENTITY_VERIFIED.getMessage();
    }

    private String handleFailedVerification(LoanReferral loanReferral, PremblyNinResponse premblyResponse) throws MeedlException {
        log.info("Identity: Loan referral id {}. Not verified", loanReferral.getId());
        createVerificationFailure(loanReferral, premblyResponse.getDetail(), ServiceProvider.PREMBLY);
        return IDENTITY_NOT_VERIFIED.getMessage();
    }
    private void decryptEncryptedIdentification(IdentityVerification identityVerification) throws MeedlException {
        String decryptedBvn = decrypt(identityVerification.getEncryptedBvn());
        String decryptedNin = decrypt(identityVerification.getEncryptedNin());
        identityVerification.setDecryptedBvn(decryptedBvn);
        identityVerification.setDecryptedNin(decryptedNin);
    }


}
