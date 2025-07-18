package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanRequestOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanMetricsMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.commons.IdentityVerificationMessage;
//import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.jose.jwk.JWK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    private AesOutputPort tokenUtils;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private LoanMetricsMapper loanMetricsMapper;
    @Autowired
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;

    @Override
    public String verifyIdentity(String loanReferralId) throws IdentityException, MeedlException {
        MeedlValidator.validateUUID(loanReferralId, "Please provide a valid loan referral identification.");
        checkIfAboveThreshold(loanReferralId);
        LoanReferral loanReferral = loanReferralOutputPort.findLoanReferralById(loanReferralId)
                                    .orElseThrow(()-> new ResourceNotFoundException("Could not find loan referral"));
        if (ObjectUtils.isEmpty(loanReferral.getLoanee())){
            throw new IdentityException("Loan referral has no loanee assigned to it.");
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
    public String verifyIdentity(String actorId,IdentityVerification identityVerification) throws MeedlException {
        LoanReferral loanReferral = validateIdentity(identityVerification);
        UserIdentity userIdentity = null;
        if (ObjectUtils.isNotEmpty(loanReferral)) {
             userIdentity = userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn());
        }else {
            userIdentity = userIdentityOutputPort.findById(actorId);
        }

        if (isVerificationRequired(userIdentity)){
            log.info("User requires identity verification");
            return processNewVerification(identityVerification, loanReferral,userIdentity);
        }else{
            log.warn("User attempted to verify again {}", loanReferral.getLoanee().getUserIdentity().getEmail());
//            processAnotherVerification(identityVerification, loanReferral);
//            addedToLoaneeLoan(loanReferral.getId());
//            log.info("Verification previously done and was successful");
                return IDENTITY_VERIFIED.getMessage();
        }
    }
    private LoanReferral validateIdentity(IdentityVerification identityVerification) throws MeedlException {
        validateIdentityVerification(identityVerification);
        decryptEncryptedIdentification(identityVerification);

        if (identityVerification.getLoanReferralId() != null) {
            LoanReferral loanReferral = fetchLoanReferral(identityVerification.getLoanReferralId());
            checkIfAboveThreshold(loanReferral.getId());
            return loanReferral;
        }
        return null;
    }

    private void updateLoaneeDetail(IdentityVerification identityVerification, String userId, PremblyNinResponse premblyResponse) throws MeedlException {
       UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
//                userIdentityOutputPort.findById(loanReferral.getLoanee().getUserIdentity().getId());
        log.info("UserIdentity before update :  {}", userIdentity);
        UserIdentity updatedUserIdentity = identityVerificationMapper.updateUserIdentity(premblyResponse.getNinData(), userIdentity);
        updatedUserIdentity.setIdentityVerified(Boolean.TRUE);
        updatedUserIdentity.setBvn(identityVerification.getEncryptedBvn());
        updatedUserIdentity.setNin(identityVerification.getEncryptedNin());
        updatedUserIdentity.setImage(null);
        log.info("update user identity from prembly {}", updatedUserIdentity);
//        identityManagerOutputPort.createUser(updatedUserIdentity);
        updatedUserIdentity = userIdentityOutputPort.save(updatedUserIdentity);
        updatedUserIdentity = updateLoaneeDetail(updatedUserIdentity);
        log.info("User identity details updated for loanee with user id : {}", updatedUserIdentity);
    }

    private UserIdentity updateLoaneeDetail(UserIdentity userIdentity) throws MeedlException {
        UserIdentity foundUser = identityManagerOutputPort.getUserById(userIdentity.getId());
        userIdentity.setEmailVerified(foundUser.isEmailVerified());
        userIdentity.setEnabled(foundUser.isEnabled());
        return identityManagerOutputPort.updateUserData(userIdentity);
    }

    private void createVerificationFailure(UserIdentity userIdentity, String message, ServiceProvider serviceProvider) throws MeedlException {
        IdentityVerificationFailureRecord identityVerificationFailureRecord = new IdentityVerificationFailureRecord();
        identityVerificationFailureRecord.setEmail(userIdentity.getEmail());
        identityVerificationFailureRecord.setReferralId(userIdentity.getId());
        identityVerificationFailureRecord.setServiceProvider(serviceProvider);
        identityVerificationFailureRecord.setReason(message);
        createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
    }

    private void checkIfAboveThreshold(String loanReferralId) throws IdentityException {
        Long numberOfAttempts = identityVerificationFailureRecordOutputPort.countByReferralId(loanReferralId);
        if (numberOfAttempts >= 5L){
            log.error("You have reached the maximum number of verification attempts for this referral code: {}", loanReferralId);
            throw new IdentityException(String.format("You have reached the maximum number of verification attempts for this referral code: %s", loanReferralId));
        }
    }

    @Override
    public String createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord identityVerificationFailureRecord) throws IdentityException {
        identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
        Long numberOfFailedVerifications = identityVerificationFailureRecordOutputPort.countByReferralId(identityVerificationFailureRecord.getReferralId());
        if (numberOfFailedVerifications >= 5){
            log.error("Number of failure verification exceeded for {}", identityVerificationFailureRecord.getReferralId());
            throw new IdentityException(BLACKLISTED_REFERRAL.getMessage());
        }
        log.info("Verification failure saved successfully for {}", identityVerificationFailureRecord.getReferralId());
        return IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage();
    }

    private void validateIdentityVerification(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification, IdentityVerificationMessage.IDENTITY_VERIFICATION_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateDataElement(identityVerification.getEncryptedBvn(), IdentityVerificationMessage.INVALID_BVN.getMessage());
        MeedlValidator.validateDataElement(identityVerification.getEncryptedNin(), IdentityVerificationMessage.INVALID_NIN.getMessage());
//        MeedlValidator.validateUUID(identityVerification.getLoanReferralId(), LoanMessages.INVALID_LOAN_REFERRAL_ID.getMessage());
//        log.info("Verifying user identity. Loan referral id: {}", identityVerification.getLoanReferralId());
    }

    private String decrypt(String encryptedData) throws MeedlException {
        log.info("decrypting identity verification values.");
        return tokenUtils.decryptAES(encryptedData, "Error processing identity verification");
    }

    private LoanReferral fetchLoanReferral(String loanReferralId) throws MeedlException {
        Optional<LoanReferral> loanReferral = loanReferralOutputPort.findLoanReferralById(loanReferralId);
        log.info("Loanee referred : {}",loanReferral.get().getLoanee().getUserIdentity().getEmail());
        return loanReferral.get();
    }

    private boolean isVerificationRequired(UserIdentity userIdentity) {
        log.info("Checking if user was found by bvn or has previously done verification. {}", userIdentity);
        return ObjectUtils.isEmpty(userIdentity) || !userIdentity.isIdentityVerified();
    }

    private String processNewVerification(IdentityVerification identityVerification,LoanReferral loanReferral,UserIdentity userIdentity) throws MeedlException {
        String verificationResponse;
        try {
            log.info("Identity verification process ongoing. ");
            PremblyNinResponse premblyNinResponse = ninLikenessVerification(identityVerification, userIdentity); // Checked
            if (premblyNinResponse.isLikenessCheckSuccessful()){
                log.info("Proceeding to bvn verification");
                PremblyBvnResponse  premblyBvnResponse = bvnLikenessVerification(identityVerification, userIdentity);  // Checked
                if (premblyBvnResponse.isLikenessCheckSuccessful()){
                    handleSuccessfulVerification(identityVerification, userIdentity.getId(), premblyNinResponse);
                    verificationResponse = IdentityMessages.IDENTITY_VERIFIED.getMessage();

                    if (ObjectUtils.isNotEmpty(loanReferral)) {
                        log.info("verification done successfully. {}", verificationResponse);
                        log.info("about to increase loan request count  {}", loanReferral);
                        log.info("refer by {}", loanReferral.getReferredBy());
                        updateLoanMetricsLoanRequestCount(loanReferral.getReferredBy());
                        log.info("done with loan request count on {}", loanReferral.getReferredBy());
                    }

                }else {
                    log.warn("Identity verification not successful, failed at the bvn level");
                    verificationResponse = IdentityMessages.IDENTITY_NOT_VERIFIED.getMessage();
                }
            }else {
                log.warn("Identity verification not successful, failed at the nin level");
                verificationResponse = IdentityMessages.IDENTITY_NOT_VERIFIED.getMessage();
            }
        } catch (IdentityException exception) {
            log.error("Error verifying user's identity... {}", exception.getMessage());
            createVerificationFailure(userIdentity, exception.getMessage(), ServiceProvider.PREMBLY);
            throw new IdentityException(exception.getMessage());
        }
        return verificationResponse;
    }

    private void updateLoanMetricsLoanRequestCount(String referBy) throws MeedlException {
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(referBy);
        if (organization.isEmpty()) {
            throw new ResourceNotFoundException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organization.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new ResourceNotFoundException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanRequestCount(
                loanMetrics.get().getLoanRequestCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
    }

    private PremblyNinResponse ninLikenessVerification(IdentityVerification identityVerification,UserIdentity loanReferral) throws MeedlException {
        PremblyNinResponse premblyNinResponse =
                identityVerificationOutputPort.verifyNinLikeness(identityVerification);
        log.info("prembly nin response: {}", premblyNinResponse);
        if ((premblyNinResponse.getVerification() != null && premblyNinResponse.getVerification().getStatus().equals("VERIFIED") && premblyNinResponse.getNinData()!= null &&
                premblyNinResponse.getFaceData() != null && premblyNinResponse.getFaceData().isFaceVerified())) {
            log.info("NIN: Identity verified successfully with NIN. {}", premblyNinResponse.getNinData());
            premblyNinResponse.setLikenessCheckSuccessful(Boolean.TRUE);
        } else {
            log.error("FAILED: Identity verification failed with NIN.");
            premblyNinResponse.setLikenessCheckSuccessful(Boolean.FALSE);
            handleFailedVerification(loanReferral, premblyNinResponse);
        }
        return premblyNinResponse;
    }

    private PremblyBvnResponse bvnLikenessVerification(IdentityVerification identityVerification, UserIdentity userIdentity) throws MeedlException {
        PremblyBvnResponse premblyBvnResponse =
                identityVerificationOutputPort.verifyBvnLikeness(identityVerification);
        log.info("prembly bvn response at identity verification service: {}", premblyBvnResponse);
        if ((premblyBvnResponse.getVerification() != null && premblyBvnResponse.getVerification().getStatus().equals("VERIFIED") && premblyBvnResponse.getData()!= null &&
                premblyBvnResponse.getData().getFaceData() != null && premblyBvnResponse.getData().getFaceData().isFaceVerified())) {
            log.info("BVN: Identity verified successfully with BVN. {}", premblyBvnResponse.getData());
            premblyBvnResponse.setLikenessCheckSuccessful(Boolean.TRUE);
        } else {
            log.error("FAILED: Identity verification failed with BVN.");
            premblyBvnResponse.setLikenessCheckSuccessful(Boolean.FALSE);
            handleFailedVerification(userIdentity, premblyBvnResponse);
        }
        return premblyBvnResponse;
    }

//    private String processAnotherVerification(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
//        PremblyNinResponse premblyNinResponse;
//        try{
////            PremblyResponse premblyResponse =
////                    identityVerificationOutputPort.verifyBvn(identityVerification);
////                    PremblyNinResponse premblyNinResponse =
////                            identityVerificationOutputPort.verifyNinLikeness(identityVerification);
//                     premblyNinResponse =
//                            identityVerificationOutputPort.verifyNinLikeness(identityVerification);
//            log.info("prembly bvn response: {}", premblyNinResponse);
//
//            log.info("Identity verification process. {}", (premblyNinResponse.getVerification().getStatus().equals("VERIFIED") && premblyNinResponse.getNinData() != null &&
//                    premblyNinResponse.getFaceData() != null && premblyNinResponse.getFaceData().isFaceVerified()));
//
//            if (premblyNinResponse.getVerification().getStatus().equals("VERIFIED") && premblyNinResponse.getNinData() != null &&
//            premblyNinResponse.getFaceData() != null && premblyNinResponse.getFaceData().isFaceVerified()) {
//                 handleSuccessfulVerification(identityVerification,loanReferral,premblyNinResponse);
//            }else {
//                 handleFailedVerification(loanReferral,premblyNinResponse);
//            }
//        }catch (MeedlException exception){
//            log.error("Error verifying user's identity... {}", exception.getMessage());
//            createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
//            throw new MeedlException(exception.getMessage());
//        }
//        return premblyNinResponse.getVerificationResponse();
//    }
//    private String handleAnotherVerification(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyBvnResponse premblyBvnResponse) throws MeedlException {
//        log.info("prembly bvn response: {}", premblyBvnResponse);
//
//        log.info("Identity verification process. {}", (premblyBvnResponse.getVerification().getStatus().equals("VERIFIED") && premblyBvnResponse.getData() != null &&
//                premblyBvnResponse.getData().getFaceData() != null && premblyBvnResponse.getData().getFaceData().isFaceVerified()));
//
//        if (premblyBvnResponse.getVerification().getStatus().equals("VERIFIED") && premblyBvnResponse.getData() != null &&
//                premblyBvnResponse.getData().getFaceData() != null && premblyBvnResponse.getData().getFaceData().isFaceVerified()) {
//            return handleSuccessfulVerification(identityVerification, loanReferral, premblyBvnResponse);
//        }else {
//            return handleFailedVerification(loanReferral, premblyBvnResponse);
//        }
//    }

//    private boolean isIdentityVerified(PremblyResponse premblyResponse) {
//        return premblyResponse.getVerification() != null &&
//                "VERIFIED".equals(premblyResponse.getVerification().getStatus());
//    }

    private void handleSuccessfulVerification(IdentityVerification identityVerification, String userId, PremblyNinResponse premblyResponse) throws MeedlException {
        log.info("Identity verified successfully. {}", premblyResponse);
        updateLoaneeDetail(identityVerification, userId , premblyResponse);
        addedToLoaneeLoan(identityVerification.getLoanReferralId());
        log.info("Identity is verified: Loan referral id {}. Verified", identityVerification.getLoanReferralId());
    }

    private void handleFailedVerification(UserIdentity userIdentity, PremblyResponse premblyResponse) throws MeedlException {
        log.info("Identity verification failed. {}", premblyResponse);
//        log.info("Identity: Loan referral id {}. Not verified", userIdentity.getId());
        createVerificationFailure(userIdentity, premblyResponse.getDetail(), ServiceProvider.PREMBLY);
        premblyResponse.setVerificationResponse(IDENTITY_NOT_VERIFIED.getMessage());
    }
    private void decryptEncryptedIdentification(IdentityVerification identityVerification) throws MeedlException {
        String decryptedBvn = decrypt(identityVerification.getEncryptedBvn());
        String decryptedNin = decrypt(identityVerification.getEncryptedNin());
        identityVerification.setDecryptedBvn(decryptedBvn);
        identityVerification.setDecryptedNin(decryptedNin);
    }
}
