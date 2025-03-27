package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyFaceData;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.Verification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.commons.IdentityVerificationMessage;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@Slf4j
@Component
public class AutomationTestService {
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private IdentityVerificationMapper identityVerificationMapper;
    @Autowired
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Autowired
    private TokenUtils tokenUtils;

    private void addedToLoaneeLoan(String loanReferralId) {
        log.info("Added to Loanee loan to loanee's list of loans {} ", loanReferralId);
    }
    private LoanReferral validateIdentity(IdentityVerification identityVerification) throws MeedlException {
        validateIdentityVerification(identityVerification);
        decryptEncryptedIdentification(identityVerification);

        LoanReferral loanReferral = fetchLoanReferral(identityVerification.getLoanReferralId());
        checkIfAboveThreshold(loanReferral.getId());
        return loanReferral;
    }
    private void updateLoaneeDetail(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyBvnResponse premblyResponse) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanReferral.getLoanee().getUserIdentity().getId());
        log.info("UserIdentity before update :  {}", userIdentity);
        UserIdentity updatedUserIdentity = identityVerificationMapper.updateUserIdentityForBvn(premblyResponse.getData(), userIdentity);
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
        MeedlValidator.validateObjectInstance(identityVerification, IdentityVerificationMessage.IDENTITY_VERIFICATION_CANNOT_BE_NULL.getMessage());
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
        log.info("Checking if user was found by bvn or has previously done verification. {}", userIdentity);
        return ObjectUtils.isEmpty(userIdentity) || !userIdentity.isIdentityVerified();
    }

    private String confirmIdentityVerified(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyBvnResponse premblyNinResponse) throws MeedlException {
        log.info("prembly nin response: {}", premblyNinResponse);

        log.info("Identity verification process ongoing. {}", premblyNinResponse);
        if ((premblyNinResponse.getVerification().getStatus().equals("VERIFIED") && premblyNinResponse.getData() != null &&
                premblyNinResponse.getData().getFaceData() != null && premblyNinResponse.getData().getFaceData().isFaceVerified())) {
            log.info("Identity verified successfully. {}", premblyNinResponse.getData());
            return handleSuccessfulVerification(identityVerification, loanReferral, premblyNinResponse);
        } else {
            return handleFailedVerification(loanReferral, premblyNinResponse);
        }
    }

    private String handleAnotherVerification(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyBvnResponse premblyBvnResponse) throws MeedlException {
        log.info("prembly bvn response: {}", premblyBvnResponse);

        log.info("Identity verification process. {}", (premblyBvnResponse.getVerification().getStatus().equals("VERIFIED") && premblyBvnResponse.getData() != null &&
                premblyBvnResponse.getData().getFaceData() != null && premblyBvnResponse.getData().getFaceData().isFaceVerified()));

        if (premblyBvnResponse.getVerification().getStatus().equals("VERIFIED") && premblyBvnResponse.getData() != null &&
                premblyBvnResponse.getData().getFaceData() != null && premblyBvnResponse.getData().getFaceData().isFaceVerified()) {
            return handleSuccessfulVerification(identityVerification, loanReferral, premblyBvnResponse);
        }else {
            return handleFailedVerification(loanReferral, premblyBvnResponse);
        }
    }


    private boolean isIdentityVerified(PremblyResponse premblyResponse) {
        return premblyResponse.getVerification() != null &&
                "VERIFIED".equals(premblyResponse.getVerification().getStatus());
    }

    private String handleSuccessfulVerification(IdentityVerification identityVerification, LoanReferral loanReferral, PremblyBvnResponse premblyResponse) throws MeedlException {
        log.info("Identity verified successfully. {}", premblyResponse);
        updateLoaneeDetail(identityVerification, loanReferral, premblyResponse);
        addedToLoaneeLoan(identityVerification.getLoanReferralId());
        log.info("Identity is verified: Loan referral id {}. Verified", identityVerification.getLoanReferralId());
        return IDENTITY_VERIFIED.getMessage();
    }

    private String handleFailedVerification(LoanReferral loanReferral, PremblyBvnResponse premblyResponse) throws MeedlException {
        log.info("Identity verified failed. {}", premblyResponse);
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

    public String verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        LoanReferral loanReferral = validateIdentity(identityVerification);
        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn());

        if (isVerificationRequired(userIdentity)){
            return processNewVerificationForAutomatedTest(identityVerification, loanReferral);
        }else{
            processAnotherVerificationAutomationTest(identityVerification, loanReferral);
            log.info("Verification previously done and was successful");
            addedToLoaneeLoan(loanReferral.getId());
            return IDENTITY_VERIFIED.getMessage();
        }
    }

    private String processAnotherVerificationAutomationTest(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
        try{
            PremblyBvnResponse premblyBvnResponse = createPremblyBvnTestResponse(identityVerification.getDecryptedBvn());
            return handleAnotherVerification(identityVerification, loanReferral, premblyBvnResponse);
        }catch (MeedlException exception){
            log.error("Error verifying user's identity... {}", exception.getMessage());
            createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
            throw new MeedlException(exception.getMessage());
        }
    }

    private String processNewVerificationForAutomatedTest(IdentityVerification identityVerification, LoanReferral loanReferral) throws MeedlException {
        try {
            PremblyBvnResponse premblyBvnResponse = createPremblyBvnTestResponse(identityVerification.getDecryptedBvn());
            return confirmIdentityVerified(identityVerification, loanReferral, premblyBvnResponse);
        } catch (MeedlException exception) {
            log.error("Error verifying user's identity... {}", exception.getMessage());
            createVerificationFailure(loanReferral, exception.getMessage(), ServiceProvider.PREMBLY);
            throw new MeedlException(exception.getMessage());
        }
    }
    public static PremblyBvnResponse createPremblyBvnTestResponse(String bvn) {
        return PremblyBvnResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .data(PremblyBvnResponse.BvnData.builder()
                        .bvn(bvn)
                        .firstName("automatedTest")
                        .middleName("automatedTest")
                        .lastName("automatedTest")
                        .dateOfBirth("1990-01-01")
                        .registrationDate("2020-05-15")
                        .enrollmentBank("First Bank Automated Test")
                        .enrollmentBranch("Lagos Main Automated Test")
                        .email("john.doe@example.com")
                        .gender("Male")
                        .levelOfAccount("Tier 3")
                        .lgaOfOrigin("IkejaAutomatedTest")
                        .lgaOfResidence("SurulereAutomatedTest")
                        .maritalStatus("Single")
                        .nin("12345678910")
                        .nameOnCard("John D. Smith AutomatedTest")
                        .nationality("Nigerian")
                        .phoneNumber1("+2348012345678")
                        .phoneNumber2("+2348098765432")
                        .residentialAddress("123, Lagos Street, Ikeja AutomatedTest")
                        .stateOfOrigin("Lagos")
                        .stateOfResidence("Lagos")
                        .title("Mr.")
                        .watchListed("No")
                        .image("base64-image-string")
                        .number("12345")
                        .faceData(createMockFaceData())
                        .build())
                .verification(createMockVerification())
                .session(null)
                .build();
    }

    public static Verification createMockVerification() {
        return Verification.builder()
                .status("VERIFIED")
                .validIdentity(true)
                .reference("REF-123456345")
                .build();
    }
    public static PremblyFaceData createMockFaceData() {
        return PremblyFaceData.builder()
                .faceVerified(true)
                .message("Face Match")
                .confidence("99.9987564086914")
                .responseCode("00")
                .build();
    }


}
