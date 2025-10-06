package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanRequestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.LoanMetricsMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.commons.IdentityVerificationMessage;
//import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.IDENTITY_NOT_VERIFIED;

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
    @Autowired
    private ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    @Autowired
    private OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    @Autowired
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private DemographyOutputPort demographyOutputPort;

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
        log.info("identity verification == {}", identityVerification);
        checkIfAboveThreshold(actorId);
        validateIdentityVerification(identityVerification);
        decryptEncryptedIdentification(identityVerification);
        UserIdentity userIdentity = userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn());
        if (ObjectUtils.isEmpty(userIdentity)) {
            log.info("unable to find user by bvn for verification, searching with user id {}", actorId);
            userIdentity = userIdentityOutputPort.findById(actorId);
        }
        if (isVerificationRequired(userIdentity)){
            log.info("User requires identity verification");
            log.info("user identity === -- == {}",userIdentity);
            return processNewVerification(identityVerification,userIdentity);
        }else{
            log.warn("User attempted to verify again {}", userIdentity.getEmail());
//            processAnotherVerification(identityVerification, loanReferral);
//            addedToLoaneeLoan(loanReferral.getId());
//            log.info("Verification previously done and was successful");
                return IDENTITY_VERIFIED.getMessage();
        }
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
        identityVerificationFailureRecord.setUserId(userIdentity.getId());
        identityVerificationFailureRecord.setServiceProvider(serviceProvider);
        identityVerificationFailureRecord.setReason(message);
        createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
    }

    private void checkIfAboveThreshold(String actorId) throws IdentityException {
        Long numberOfAttempts = identityVerificationFailureRecordOutputPort.countByUserId(actorId);
        if (numberOfAttempts >= 5L){
            log.error("You have reached the maximum number of verification attempts for this user: {}", actorId);
            throw new IdentityException(String.format("You have reached the maximum number of verification attempts for this user code: %s", actorId));
        }
    }

    @Override
    public String createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord identityVerificationFailureRecord) throws IdentityException {
        identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
        Long numberOfFailedVerifications = identityVerificationFailureRecordOutputPort.countByUserId(identityVerificationFailureRecord.getUserId());
        if (numberOfFailedVerifications >= 5){
            log.error("Number of failure verification exceeded for {}", identityVerificationFailureRecord.getUserId());
            throw new IdentityException(BLACKLISTED_REFERRAL.getMessage());
        }
        log.info("Verification failure saved successfully for {}", identityVerificationFailureRecord.getUserId());
        return IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage();
    }

    private void validateIdentityVerification(IdentityVerification identityVerification) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityVerification, IdentityVerificationMessage.IDENTITY_VERIFICATION_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateDataElement(identityVerification.getEncryptedBvn(), IdentityVerificationMessage.INVALID_BVN.getMessage());
        MeedlValidator.validateDataElement(identityVerification.getEncryptedNin(), IdentityVerificationMessage.INVALID_NIN.getMessage());
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

    private String processNewVerification(IdentityVerification identityVerification,UserIdentity userIdentity) throws MeedlException {
        String verificationResponse;
        try {
            log.info("Identity verification process ongoing. user identity {} /n identity verification == {}",userIdentity,identityVerification);
            PremblyNinResponse premblyNinResponse = ninLikenessVerification(identityVerification, userIdentity); // Checked
            if (premblyNinResponse.isLikenessCheckSuccessful()){
                log.info("Proceeding to bvn verification");
                PremblyBvnResponse  premblyBvnResponse = bvnLikenessVerification(identityVerification, userIdentity);  // Checked
                if (premblyBvnResponse.isLikenessCheckSuccessful()){
                    handleSuccessfulVerification(identityVerification, userIdentity.getId(), premblyNinResponse);
                    verificationResponse = IdentityMessages.IDENTITY_VERIFIED.getMessage();

                    log.info("verification done successfully. {}", verificationResponse);
                    makeUpdateToUserLoaNReferrals(userIdentity);

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

    private void makeUpdateToUserLoaNReferrals(UserIdentity userIdentity) throws MeedlException {
        List<LoanReferral> loanReferrals =
                loanReferralOutputPort.findAllLoanReferralsByUserIdAndStatus(userIdentity.getId(),
                        LoanReferralStatus.AUTHORIZED);
        log.info("Found {} loanReferrals", loanReferrals.size());
        log.info("Loan referrals found {}",loanReferrals);updateLoanRequestCountForEachLoanReferralThatHasBeenAccepted(loanReferrals);
    }

    private void updateLoanRequestCountForEachLoanReferralThatHasBeenAccepted(List<LoanReferral> loanReferrals) throws MeedlException {
        for (LoanReferral loanReferral : loanReferrals) {
            log.info("about to increase loan request count  {}", loanReferral);
            log.info("refer by {}", loanReferral.getCohortLoanee().getReferredBy());
            updateLoanMetricsLoanRequestCount(loanReferral.getCohortLoanee().getReferredBy());
            log.info("done with loan request count on loan referral by  {}", loanReferral.getCohortLoanee().getReferredBy());
            Cohort cohort = updateLoanRequestCountOnCohort(loanReferral);

            updateLoanAmountRequestedOnCohortLoanDetail(loanReferral.getCohortLoanee().getLoaneeLoanDetail(), cohort);

            updateLoanAmountRequestedOnProgramLoanDetail(loanReferral.getCohortLoanee().getLoaneeLoanDetail(), cohort);

            updateLoanAmountRequestOnOrganizationLoanDetail(loanReferral.getCohortLoanee().getLoaneeLoanDetail(), cohort);
        }
    }

    private void updateLoanAmountRequestOnOrganizationLoanDetail(LoaneeLoanDetail loaneeLoanDetail, Cohort cohort) throws MeedlException {
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId());
        organizationLoanDetail.setAmountRequested(organizationLoanDetail.getAmountRequested()
                .add(loaneeLoanDetail.getAmountRequested()));
        organizationLoanDetailOutputPort.save(organizationLoanDetail);
    }

    private void updateLoanAmountRequestedOnProgramLoanDetail(LoaneeLoanDetail loaneeLoanDetail, Cohort cohort) throws MeedlException {
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(cohort.getProgramId());
        log.info("program loan details id {}", programLoanDetail.getId());
        programLoanDetail.setAmountRequested(programLoanDetail.getAmountRequested()
                .add(loaneeLoanDetail.getAmountRequested()));
        programLoanDetailOutputPort.save(programLoanDetail);
    }

    private void updateLoanAmountRequestedOnCohortLoanDetail(LoaneeLoanDetail loaneeLoanDetail, Cohort cohort) throws MeedlException {
        CohortLoanDetail foundCohort = cohortLoanDetailOutputPort.findByCohortId(cohort.getId());
        log.info("current total amount requested for cohort {}", foundCohort.getAmountRequested());
        log.info("loanee amount requested {}", loaneeLoanDetail.getAmountRequested());
        foundCohort.setAmountRequested(foundCohort.getAmountRequested().
                add(loaneeLoanDetail.getAmountRequested()));
        cohortLoanDetailOutputPort.save(foundCohort);
        log.info("total amount requested updated for cohort after adding == {} is {}",
                loaneeLoanDetail.getAmountRequested(), foundCohort.getAmountRequested());
    }

    private Cohort updateLoanRequestCountOnCohort(LoanReferral loanReferral) throws MeedlException {
        log.info("Updating number of loan request on cohort: {}", loanReferral.getCohortLoanee());
        Cohort cohort = loanReferral.getCohortLoanee().getCohort();
        log.info("found cohort == {}", cohort);
        log.info("current number of loan request == {}", cohort.getNumberOfLoanRequest());
        cohort.setNumberOfLoanRequest(cohort.getNumberOfLoanRequest() + 1);
        cohort = cohortOutputPort.save(cohort);
        log.info(" number of loan request after adding 1 == {}", cohort.getNumberOfLoanRequest());
        return cohort;
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

    private PremblyNinResponse ninLikenessVerification(IdentityVerification identityVerification,UserIdentity userIdentity) throws MeedlException {
        log.info("prembly nin likeness verification about to start  {}",identityVerification);
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
            handleFailedVerification(userIdentity, premblyNinResponse);
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
        updateDemography(premblyResponse);
    }

    private void updateDemography(PremblyNinResponse premblyResponse) throws MeedlException {

        Demography demography = demographyOutputPort.findDemographyByName(MEEDL);
        updateGenderCount(premblyResponse.getNinData().getGender(),demography);

        updateAgeRangeCount(premblyResponse.getNinData().getBirthDate(),demography);

        updateStateOfOriginCount(premblyResponse.getNinData(),demography);

        demographyOutputPort.save(demography);

        //TODO
        //EDUCATIONAL LEVEL
    }

    private String normalizeStateName(String state) {
        return state.replace(" State", "").replace(" state", "").trim().toLowerCase();
    }

    private void updateStateOfOriginCount(PremblyNinResponse.NinData ninData, Demography demography) {
        String birthCountry = ninData.getBirthCountry();
        if (!NIGERIA.equalsIgnoreCase(birthCountry)) {
            demography.setNonNigerian(demography.getNonNigerian() + ONE);
        } else {
            String birthState = normalizeStateName(ninData.getBirthState().trim());
            if (SOUTH_EAST.contains(birthState)) {
                demography.setSouthEastCount(demography.getSouthEastCount() + ONE);
            } else if (SOUTH_WEST.contains(birthState)) {
                demography.setSouthWestCount(demography.getSouthWestCount() + ONE);
            } else if (SOUTH_SOUTH.contains(birthState)) {
                demography.setSouthSouthCount(demography.getSouthSouthCount() + ONE);
            } else if (NORTH_EAST.contains(birthState)) {
                demography.setNorthEastCount(demography.getNorthEastCount() + ONE);
            } else if (NORTH_WEST.contains(birthState)) {
                demography.setNorthWestCount(demography.getNorthWestCount() + ONE);
            } else if (NORTH_CENTRAL.contains(birthState)) {
                demography.setNorthCentralCount(demography.getNorthCentralCount() + ONE);
            }
        }
    }

    private void updateAgeRangeCount(String birthDate, Demography demography) {
        LocalDate birthLocalDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        int age = Period.between(birthLocalDate, LocalDate.now()).getYears();
        if (age >= 17 && age <= 25) {
            demography.setAge17To25Count(demography.getAge17To25Count() + ONE);
        } else if (age >= 26 && age <= 35) {
            demography.setAge25To35Count(demography.getAge25To35Count() + ONE);
        } else if (age >= 36 && age <= 45) {
            demography.setAge35To45Count(demography.getAge35To45Count() + ONE);
        }
    }

    private void updateGenderCount(String gender, Demography demography){
        String normalizeGender = GENDER_TO_FULL.get(gender.toLowerCase());
        if (normalizeGender.equalsIgnoreCase(MALE)){
            demography.setMaleCount(demography.getMaleCount() + ONE);
        }else if (normalizeGender.equalsIgnoreCase(FEMALE)){
            demography.setFemaleCount(demography.getFemaleCount() + ONE);
        }
        demography.setTotalGenderCount(demography.getTotalGenderCount() + ONE);
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
