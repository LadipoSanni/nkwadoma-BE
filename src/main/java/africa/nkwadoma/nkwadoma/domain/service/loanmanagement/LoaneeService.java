package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.creditregistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes.TokenUtils;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages.DROP_OUT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages.LOAN_DEFERRAL;

@Slf4j
@AllArgsConstructor
@EnableAsync
@Service
public class LoaneeService implements LoaneeUseCase {
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final UserIdentityOutputPort identityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private final ProgramOutputPort programOutputPort;
    private final TokenUtils tokenUtils;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final LoaneeEmailUsecase loaneeEmailUsecase;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final CreditRegistryOutputPort creditRegistryOutputPort;
    private final LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoanOutputPort loanOutputPort;
    private final MeedlNotificationOutputPort meedlNotificationOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final AesOutputPort aesOutputPort;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;
    private final InstituteMetricsOutputPort instituteMetricsOutputPort;


    @Override
    public List<Loanee> inviteLoanees(List<Loanee> loanees, String cohortId){
        List<Loanee> loaneesVerified = loanees.stream()
                .map(loanee -> {
                    String id = null;
                    try {
                        id = loanee.getId();
                        loanee = loaneeOutputPort.findLoaneeById(id);
                        validationsForInvitingLoanee(loanee, id);
                        loanee.setUploadedStatus(UploadedStatus.INVITED);
                        loanee = loaneeOutputPort.save(loanee);
                        log.info("Loanees found after save on loanee invite {}", loanee);
                        getLoanInCohort(loanee, cohortId);
                    } catch (MeedlException e) {
                        log.error("Loanee with id doesn't exist");
                        notifyPmLoaneeDoesNotExist(e.getMessage(), id);
                    }
                    log.info("Loanee to be invited to the platform is {}", loanee);
                    return loanee;
                })
                .filter(Objects::nonNull).toList();

         sendLoaneesEmail(loaneesVerified);
         return loaneesVerified;
    }

    private void getLoanInCohort(Loanee loanee, String cohortId) {

        try {
            CohortLoanee cohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loanee.getId(), cohortId);
//            LoaneeLoanDetail loaneeLoanDetail = loaneeLoanDetailsOutputPort.findByCohortLoaneeId(cohortLoanee.getId());
            loanee.setCohortLoaneeId(cohortLoanee.getId());
            log.info("Cohort loanee found during the email loanee invite is {}", cohortLoanee);
        } catch (MeedlException e) {
            log.error("Unable to find loan referral for loanee with id {}", loanee.getId());
        }
    }

    private void validationsForInvitingLoanee(Loanee loanee, String id) throws MeedlException {
        if (loanee == null){
            log.error("Loanee not found for loanee with id : {}", id);
            throw new LoanException("Loanee not found with id : "+ id);
        }
        if (ObjectUtils.isEmpty(loanee.getUserIdentity())){
            log.error("Unable to determine loanee identity. User identity is null.");
            throw new MeedlException("Unable to determine loanee identity.");
        }
        if (!loanee.getUserIdentity().getRole().equals(IdentityRole.LOANEE)){
            log.warn("The user is not a loanee but {}", loanee.getUserIdentity().getRole());
            throw new LoanException("User with id "+ id +" is not a loanee");
        }
        UserIdentity userIdentity = identityManagerOutputPort.getUserByEmail(loanee.getUserIdentity().getEmail())
                .orElseThrow(()-> new MeedlException("Loanee does not exist on the platform"));
        if (userIdentity.isEnabled()){
            log.error("User with email {} is already active on th platform", userIdentity.getEmail());
            throw new LoanException("User with email "+userIdentity.getEmail() +" is already active on the platform.");
        }

        if (!loanee.getOnboardingMode().equals(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)) {
            log.warn("The loanee being invited is not from file upload {}", userIdentity.getEmail());
        }
    }

    private void sendLoaneesEmail(List<Loanee> loanees) {
        log.info("About to start sending invites to the loanees. {}",loanees);
        asynchronousMailingOutputPort.sendLoaneeInvite(loanees);
    }

    private void notifyPmLoaneeDoesNotExist(String message, String email) {
        log.warn("Pm is not warned that loanee invited does not exist on the platform and is trying to be invited. {} --- {}", email, message);
    }

    @Override
    public Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException {
        log.info("Validating loanee before adding");
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        loanee.validate();
        loanee.getLoaneeLoanDetail().validate();
        CohortLoanee cohortLoanee = CohortLoanee.builder().build();
        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        Loanee existingLoanee = checkIfLoaneeWithEmailExist(loanee);
        checkIfCohortTuitionDetailsHaveBeenUpdated(cohort);
        checkIfInitialDepositIsNotGreaterThanTotalCohortFee(loanee, cohort);
        BigDecimal totalLoanBreakDown = getTotalLoanBreakdown(loanee);
        validateAmountRequested(loanee, totalLoanBreakDown, cohort);
        cohortLoanee = addExistingLoaneeToCohort(loanee, existingLoanee, cohort, cohortLoanee);
        cohortLoanee = addNewLoaneeToCohort(loanee, existingLoanee, cohortLoanee, cohort);
        cohortLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
        log.info("Successfully added loanee = = {} ", cohortLoanee);
        saveLoaneeLoanBreakdowns(loanee, cohortLoanee);
        updateCohortValues(cohort);
        return cohortLoanee.getLoanee();
    }

    @Override
    public LoaneeLoanDetail viewLoaneeLoanDetail(String cohortLoaneeId) throws MeedlException {
        MeedlValidator.validateUUID(cohortLoaneeId, "Provide valid loanee loan detail id");
        log.info("Finding loanee loan details with cohort loanee id  {}", cohortLoaneeId);
        return loaneeLoanDetailsOutputPort.findByCohortLoaneeId(cohortLoaneeId);
    }
    private void validateAmountRequested(Loanee loanee, BigDecimal totalLoanBreakDown, Cohort cohort) throws MeedlException {
        calculateAmountRequested(loanee, totalLoanBreakDown, cohort);
        checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(loanee, cohort);
    }

    private void saveLoaneeLoanBreakdowns(Loanee loanee, CohortLoanee cohortLoanee) throws MeedlException {
        List<LoaneeLoanBreakdown> savedLoaneeLoanbreakDowns = loaneeLoanBreakDownOutputPort.saveAll(loanee.getLoanBreakdowns(), cohortLoanee);
        cohortLoanee.getLoanee().setLoanBreakdowns(savedLoaneeLoanbreakDowns);
    }

    private void updateCohortValues(Cohort cohort) throws MeedlException {
        cohort.setNumberOfLoanees(cohort.getNumberOfLoanees() + 1);
        cohort.setStillInTraining(cohort.getStillInTraining() + 1);
        increaseNumberOfLoaneesInProgram(cohort, 1);
        increaseNumberOfLoaneesInOrganization(cohort, 1);
        cohortOutputPort.save(cohort);
    }

    private CohortLoanee addExistingLoaneeToCohort(Loanee loanee, Loanee existingLoanee, Cohort cohort, CohortLoanee cohortLoanee) throws MeedlException {
        if (ObjectUtils.isNotEmpty(existingLoanee)){
            checkIfLoaneeExistInCohort(cohort, existingLoanee);
             checkIfLoaneeExistInAnActiveCohortInSameProgram(existingLoanee, cohort);
            existingLoanee.setLoaneeLoanDetail(loanee.getLoaneeLoanDetail());
            cohortLoanee = addLoaneeToCohort(existingLoanee, cohort);
            cohortLoanee.setCreatedBy(loanee.getUserIdentity().getCreatedBy());
        }
        return cohortLoanee;
    }

    private CohortLoanee addNewLoaneeToCohort(Loanee loanee, Loanee existingLoanee, CohortLoanee cohortLoanee, Cohort cohort) throws MeedlException {
        if (ObjectUtils.isEmpty(existingLoanee)){
            loanee.getUserIdentity().setRole(IdentityRole.LOANEE);
            loanee.setActivationStatus(ActivationStatus.ACTIVE);
            loanee.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
            loanee.setUploadedStatus(UploadedStatus.ADDED);
            cohortLoanee = addLoaneeToCohort(loanee, cohort);
            loanee.setCreatedAt(LocalDateTime.now());
            Loanee createdLoanee = createLoaneeAccount(loanee);
            cohortLoanee.setLoanee(createdLoanee);
            setUpLoaneeLoanAggregate(createdLoanee);
        }
        return cohortLoanee;
    }

    private void setUpLoaneeLoanAggregate(Loanee createdLoanee) throws MeedlException {
        LoaneeLoanAggregate loaneeLoanAggregate = LoaneeLoanAggregate.builder()
                .loanee(createdLoanee)
                .historicalDebt(BigDecimal.ZERO)
                .numberOfLoans(0)
                .totalAmountOutstanding(BigDecimal.ZERO)
                .totalAmountRepaid(BigDecimal.ZERO).build();
        loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
    }

    private CohortLoanee addLoaneeToCohort(Loanee loanee, Cohort cohort) throws MeedlException {
        CohortLoanee cohortLoanee = CohortLoanee.builder().createdBy(loanee.getUserIdentity().getCreatedBy())
                .createdAt(LocalDateTime.now())
                .cohort(cohort)
                .loanee(loanee)
                .loaneeStatus(LoaneeStatus.ADDED)
                .build();
        LoaneeLoanDetail loaneeLoanDetail = saveLoaneeLoanDetails(loanee.getLoaneeLoanDetail());
        cohortLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
        return cohortLoanee;
    }

    private void checkIfLoaneeExistInAnActiveCohortInSameProgram(Loanee loanee, Cohort cohort) throws MeedlException {
        CohortLoanee cohortLoanee =
                cohortLoaneeOutputPort.findCohortLoaneeByProgramIdAndLoaneeId(cohort.getProgramId(), loanee.getId());
        if(ObjectUtils.isNotEmpty(cohortLoanee)){
        if (cohortLoanee.getCohort().getCohortStatus().equals(CohortStatus.CURRENT)
                && cohortLoanee.getCohort().getActivationStatus().equals(ActivationStatus.ACTIVE)){
            throw new EducationException(CohortMessages.LOANEE_STILL_IN_AN_ACTIVE_COHORT.getMessage());
        }
        }
    }

    private void checkIfLoaneeExistInCohort(Cohort cohort, Loanee loaneeExist) throws MeedlException {
        CohortLoanee cohortLoanee =
                cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loaneeExist.getId(), cohort.getId());
        if (ObjectUtils.isNotEmpty(cohortLoanee)){
            throw new EducationException(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage());
        }
    }

    @Override
    public void increaseNumberOfLoaneesInOrganization(Cohort cohort, int size) throws MeedlException {
        InstituteMetrics instituteMetrics =
                instituteMetricsOutputPort.findByOrganizationId(cohort.getOrganizationId());

        instituteMetrics.setNumberOfLoanees(instituteMetrics.getNumberOfLoanees() + size);
        instituteMetrics.setStillInTraining(instituteMetrics.getStillInTraining() + size);
        instituteMetricsOutputPort.save(instituteMetrics);
        log.info("Total number of loanees in an institute has been increased to : {}, in institute with id : {}",
                instituteMetrics.getNumberOfLoanees(), instituteMetrics.getId());
    }

    @Override
    public void increaseNumberOfLoaneesInProgram(Cohort cohort, int size) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        log.info("Number of loanees in program found is :: {}", program.getNumberOfLoanees());
        program.setNumberOfLoanees(program.getNumberOfLoanees() + size);
        program = programOutputPort.saveProgram(program);
        log.info("Total number of loanees in a program has been increased to : {}, in program with id : {}", program.getNumberOfLoanees(), program.getId());
    }

    @Override
    public Loanee viewLoaneeDetails(String loaneeId, String userId) throws MeedlException {
        Loanee loanee = null;
        if (!isLoanee(userId)) {
            MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
            loanee = loaneeOutputPort.findLoaneeById(loaneeId);
        } else {
            Optional<Loanee> optionalLoanee = loaneeOutputPort.findByUserId(userId);
            if (optionalLoanee.isEmpty()) {
                throw new MeedlException(LoaneeMessages.LOANEE_NOT_FOUND.getMessage());
            }
            loanee = optionalLoanee.get();
        }

        return updateLoaneeCreditScore(loanee);
    }

    private boolean isLoanee(String userId) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        return userIdentity.getRole().equals(IdentityRole.LOANEE);
    }

    private Loanee updateLoaneeCreditScore(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity(), UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        log.info("Loanee bvn before view loanee is . {}", loanee.getUserIdentity().getBvn());
        if (MeedlValidator.isNotEmptyString(loanee.getUserIdentity().getBvn())) {
            if (ObjectUtils.isEmpty(loanee.getCreditScoreUpdatedAt()) ||
                    creditScoreIsAboveOrEqualOneMonth(loanee)) {
                loanee = updateCreditScore(loanee);
            }
        }

        log.info("Credit score for loanee with id {} has already been updated within the last month", loanee.getId());

        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        loanee.setCohortName(cohort.getName());
        loanee.setCohortStartDate(cohort.getStartDate());
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        loanee.setProgramName(program.getName());

        LoanOffer loanOffer = loanOfferOutputPort.findLoanOfferByLoaneeId(loanee.getId());
        if (loanOffer != null){
            loanee.setTenor(loanOffer.getLoanProduct().getTenor());
            loanee.setTermsAndConditions(loanOffer.getLoanProduct().getTermsAndCondition());
            loanee.setInterestRate(loanOffer.getLoanProduct().getInterestRate());
            loanee.setPaymentMoratoriumPeriod(loanOffer.getLoanProduct().getMoratorium());
        }
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(cohort.getOrganizationId());
        loanee.setInstitutionName(organizationIdentity.getName());
        return loanee;
    }

    private Loanee updateCreditScore(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity().getBvn(), UserMessages.BVN_CANNOT_BE_EMPTY.getMessage());
        log.info("Updating credit score, for loanee with id {}. Last date updated was {}.", loanee.getId(), loanee.getCreditScoreUpdatedAt());
        log.info("Encrypted Loanee BVN: {}", loanee.getUserIdentity().getBvn());
        String decryptedBVN = aesOutputPort.decryptAES(loanee.getUserIdentity().getBvn(), "Error processing identity verification");
        log.info("Decrypted Loanee BVN: {}", decryptedBVN);

        try {
            int creditScoreWithBvn = creditRegistryOutputPort.getCreditScoreWithBvn(decryptedBVN);
            loanee.setCreditScore(creditScoreWithBvn);
            loanee.setCreditScoreUpdatedAt(LocalDateTime.now());
            return loaneeOutputPort.save(loanee);
        } catch (MeedlException e) {
            log.error("Exception occurred while trying to update credit score, before viewing loanee details. {}", e.getMessage());
            return loanee;
        }
    }

    private boolean creditScoreIsAboveOrEqualOneMonth(Loanee loanee) {
        Duration duration = Duration.between(loanee.getCreditScoreUpdatedAt(), LocalDateTime.now());
        log.info("Is credit score above or equal one month ago {} for loanee with id {}", duration.toDays() >= 30, loanee.getId());
        return duration.toDays() >= 30;
    }

    @Override
    public Page<CohortLoanee> viewAllLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(), CohortMessages.INVALID_COHORT_ID.getMessage());
        return cohortLoaneeOutputPort.findAllLoaneeInCohort(loanee,pageSize,pageNumber);
    }

    private LoaneeLoanDetail saveLoaneeLoanDetails(LoaneeLoanDetail loaneeLoanDetail) throws MeedlException {
        loaneeLoanDetail.validate();
        loaneeLoanDetail.setAmountRepaid(BigDecimal.ZERO);
        loaneeLoanDetail.setCreatedAt(LocalDateTime.now());
        loaneeLoanDetail.setInterestIncurred(BigDecimal.ZERO);
        loaneeLoanDetail.setAmountOutstanding(BigDecimal.ZERO);
        loaneeLoanDetail.setAmountReceived(BigDecimal.ZERO);
        return loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
    }

    @Override
    public LoanReferral referLoanee(CohortLoanee cohortLoanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohortLoanee, CohortMessages.COHORT_LOANEE_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateObjectInstance(cohortLoanee.getLoanee(), LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(cohortLoanee.getCohort(), CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(cohortLoanee.getLoanee().getOnboardingMode(), LoaneeMessages.INVALID_ONBOARDING_MODE.getMessage());

        OrganizationIdentity organizationIdentity = null;
        if (cohortLoanee.getLoanee().getOnboardingMode().equals(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)){
            organizationIdentity = getLoaneeOrganization(cohortLoanee.getCohort().getId());
        }else {
            organizationIdentity = getLoaneeOrganization(cohortLoanee.getCohort().getId());
        }
        checkIfLoaneeHasBeenReferredInTheSameCohort(cohortLoanee);
        return referLoanee(cohortLoanee, organizationIdentity);
    }

    private LoanReferral referLoanee(CohortLoanee cohortLoanee, OrganizationIdentity organizationIdentity) throws MeedlException {
        cohortLoanee.setReferredBy(organizationIdentity.getName());
        updateLoaneeReferralDetail(cohortLoanee);
        log.info("referred by {}", cohortLoanee.getReferredBy());

        LoanReferral loanReferral = buildLoanReferral(cohortLoanee);

        cohortLoanee.getCohort().setNumberOfReferredLoanee(cohortLoanee.getCohort().getNumberOfReferredLoanee() + 1);
        log.info("Cohort details gotten from cohort loanee entity {}", cohortLoanee.getCohort());
        log.info("Total number of loanees in Cohort details gotten from cohort loanee entity {}", cohortLoanee.getCohort().getNumberOfLoanees());
        cohortOutputPort.save(cohortLoanee.getCohort());
        Optional<LoanMetrics> loanMetrics = updateLoanMetrics(organizationIdentity);
        log.info("Loan metrics saved: {}", loanMetrics);
        List<LoaneeLoanBreakdown> loanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId());

        cohortLoanee.getLoanee().setLoanBreakdowns(loanBreakdowns);
        cohortLoanee.getLoanee().setLoanReferralId(loanReferral.getId());
        loanReferral.setLoanee(cohortLoanee.getLoanee());
        log.info("loan referral org == {}", loanReferral.getLoanee().getReferredBy());
        return loanReferral;
    }

    private LoanReferral buildLoanReferral(CohortLoanee cohortLoanee) throws MeedlException {
        LoanReferral loanReferral = LoanReferral.builder().cohortLoanee(cohortLoanee)
                .loanReferralStatus(LoanReferralStatus.PENDING).build();
        loanReferral.validateForCreate();
        loanReferral = loanReferralOutputPort.save(loanReferral);
        return loanReferral;
    }

    private void updateLoaneeReferralDetail(CohortLoanee cohortLoanee) throws MeedlException {
        cohortLoanee.setLoaneeStatus(LoaneeStatus.REFERRED);
        cohortLoanee.setReferralDateTime(LocalDateTime.now());
        cohortLoaneeOutputPort.save(cohortLoanee);
        log.info("saved loanee referred by {}", cohortLoanee.getReferredBy());
    }

    private Optional<LoanMetrics> updateLoanMetrics(OrganizationIdentity organizationIdentity) throws MeedlException {
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId());
        if (loanMetrics.isEmpty()) {
            throw new LoanException("No loan metrics found for organization");
        }
        LoanMetrics loanMetric = loanMetrics.get();
        loanMetric.setLoanReferralCount(
                loanMetrics.get().getLoanReferralCount() + 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
        return loanMetrics;
    }

    private void checkIfLoaneeHasBeenReferredInTheSameCohort(CohortLoanee cohortLoanee) throws MeedlException {
        checkLoaneeStatus(cohortLoanee);
        LoanReferral loanReferral =
                loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(cohortLoanee.getLoanee().getId(),
                        cohortLoanee.getCohort().getId());
        if (ObjectUtils.isNotEmpty(loanReferral)) {
            log.error("Loanee has been referred to this cohort before with error: {} ", LoaneeMessages.LOANEE_HAS_BEEN_REFERRED_BEFORE.getMessage());
            throw new LoanException(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED_BEFORE.getMessage());
        }
        log.info("Loanee has not been referred to this cohort before.");
    }
//    @Override
//    @Async
//    public void notifyLoanReferralActors(List<Loanee> loanees){
//        loanees.forEach(loanee -> {
//            try {
//                refer(loanee);
//                notifyAllPortfolioManager();
//            } catch (MeedlException e) {
//                log.warn("Error sending actor email on loan referral {}", e.getMessage());
//            }
//        });
//    }
    private void notifyAllPortfolioManager() throws MeedlException {
        for (UserIdentity userIdentity : identityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            notifyPortfolioManager(userIdentity);
        }
    }
    private void notifyPortfolioManager(UserIdentity userIdentity) throws MeedlException {
        loaneeEmailUsecase.sendLoaneeHasBeenReferEmail(userIdentity);
    }

    @Override
    public Page<CohortLoanee> searchForLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(), CohortMessages.INVALID_COHORT_ID.getMessage());
        return cohortLoaneeOutputPort.searchForLoaneeInCohort(loanee, pageSize, pageNumber);
    }

    private void checkLoaneeStatus(CohortLoanee cohortLoanee) throws MeedlException {
//        Loanee loanee;
//        List<Loanee> loanees = loaneeOutputPort.findAllLoaneesByCohortId(cohort.getId());
//        loanee = loanees.stream().filter(eachLoanee -> eachLoanee.getId().equals(loaneeId)).findFirst()
//                .orElseThrow(()-> new LoaneeException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage()));
        if (cohortLoanee.getLoaneeStatus().equals(LoaneeStatus.REFERRED)){
            throw new LoanException(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED.getMessage());
        }else if (!cohortLoanee.getLoaneeStatus().equals(LoaneeStatus.ADDED)){
            throw new LoanException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage());
        }
    }
    private OrganizationIdentity getLoaneeOrganization(String cohortId) throws MeedlException {
        return organizationIdentityOutputPort.findOrganizationByCohortId(cohortId);
    }

    private OrganizationIdentity getLoaneeOrganization(Loanee loanee) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByEmployeeId(loanee.getUserIdentity().getCreatedBy());
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
        loanee.setReferredBy(organizationIdentity.getName());
        return organizationIdentity;
    }
    private Loanee checkIfLoaneeWithEmailExist(Loanee loanee) throws MeedlException {
        Loanee existingLoanee = loaneeOutputPort.findByLoaneeEmail(loanee.getUserIdentity().getEmail());
        if (ObjectUtils.isNotEmpty(existingLoanee)) {
            log.info("Successfully confirmed user previously exist. {}",existingLoanee);
           return existingLoanee;
        }
        log.info("Successfully confirmed user does not previously exist. {}",loanee.getUserIdentity().getEmail());
        return null;
    }

    private void calculateAmountRequested(Loanee loanee, BigDecimal totalLoanBreakDown, Cohort cohort) throws LoanException {
        log.info("Calculating amount requested for loanee {}", loanee.getUserIdentity().getEmail());
        loanee.getLoaneeLoanDetail().
                setAmountRequested(totalLoanBreakDown.add(cohort.getTuitionAmount()).
                        subtract(loanee.getLoaneeLoanDetail().getInitialDeposit()));
        loanee.getLoaneeLoanDetail().setTuitionAmount(cohort.getTuitionAmount());
        if (loanee.getLoaneeLoanDetail().getAmountRequested().compareTo(BigDecimal.ZERO)  <= 0){
            log.info("Loanee amount request is zero or negative {}", loanee.getLoaneeLoanDetail().getAmountRequested());
            throw new LoanException(LoaneeMessages.LOANEE_WITH_ZERO_OR_NEGATIVE_AMOUNT_REQUEST_CANNOT_BE_ADDED_TO_COHORT.getMessage());
        }
    }

    private BigDecimal getTotalLoanBreakdown(Loanee loanee) throws MeedlException {
        for (LoaneeLoanBreakdown loaneeLoanBreakdown : loanee.getLoanBreakdowns()){
            loaneeLoanBreakdown.validate();
        }
        return loanee.getLoanBreakdowns().stream()
                .map(LoaneeLoanBreakdown::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Loanee createLoaneeAccount(Loanee loanee) throws MeedlException {
        Optional<UserIdentity> foundUserIdentity = identityManagerOutputPort.getUserByEmail(loanee.getUserIdentity().getEmail());
        if (foundUserIdentity.isPresent()) {
            log.info("User with email {} already exists. Unable to create account for loanee while attempting to add loanee to cohort", loanee.getUserIdentity().getEmail());
            throw new LoanException(IdentityMessages.USER_IDENTITY_ALREADY_EXISTS.getMessage());
        }
        UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
        userIdentity.setCreatedAt(loanee.getCreatedAt());
        userIdentity = identityOutputPort.save(userIdentity);
        log.info("User identity saved successfully with id {}. Now proceeding to save loanee ", userIdentity.getId());
        loanee.setUserIdentity(userIdentity);
        loanee = loaneeOutputPort.save(loanee);
        log.info("New loanee saved successfully loanee id : {}. ",loanee.getId());
        return loanee;
    }

    private void checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws MeedlException {
        if (loanee.getLoaneeLoanDetail().getAmountRequested().compareTo(cohort.getTotalCohortFee()) > 0) {
            log.info("{}. Cohort id: {}", CohortMessages.AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage(), cohort.getId());
            throw new LoanException(CohortMessages.AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }

    public void checkIfInitialDepositIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws LoanException {
        if (loanee.getLoaneeLoanDetail().getInitialDeposit().compareTo(cohort.getTotalCohortFee()) > 0) {
            log.info("{}. Cohort id: {}",CohortMessages.INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage(), cohort.getId());
            throw new LoanException(CohortMessages.INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }
    public void checkIfCohortTuitionDetailsHaveBeenUpdated(Cohort cohort) throws LoanException {
        if (ObjectUtils.isEmpty(cohort.getTuitionAmount())) {
            log.info("Cohort does not have any cohort tuition details. Cohort id: {}", cohort.getId());
            throw new LoanException(CohortMessages.COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED.getMessage());
        }
    }


    @Override
    public Page<CohortLoanee> viewAllLoaneeThatBenefitedFromLoanProduct(String loanProductId,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId,"Loan product id cannot be empty");
        LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
        return cohortLoaneeOutputPort.findAllLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),pageSize,pageNumber);
    }

    @Override
    public Page<CohortLoanee> searchLoaneeThatBenefitedFromLoanProduct(String loanProductId,String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId,"Loan product id cannot be empty");
        LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
        return cohortLoaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),name,pageSize,pageNumber);
    }

    @Override
    public String deferLoan(String userId, String loanId, String reasonForDeferral) throws MeedlException {
        MeedlValidator.validateUUID(userId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(loanId, LoanMessages.LOAN_ID_REQUIRED.getMessage());
        MeedlValidator.validateDataElement(reasonForDeferral, "Reason cannot be empty");

        Loan loan =
                loanOutputPort.findLoanById(loanId);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        if (!loanee.getUserIdentity().getId().equals(userId)) {
            log.info("Access denied: A loanee cannot defer another loanee");
            throw new LoanException("Access denied: A loanee cannot defer another loanee");
        }
        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        if (!cohort.getCohortStatus().equals(CohortStatus.CURRENT)){
            log.info("\"Deferral is only allowed for 'CURRENT' cohorts. This cohort's status is \"+ cohort.getCohortStatus()");
            throw new LoanException("Deferral is only allowed for 'CURRENT' cohorts. This cohort's status is "+ cohort.getCohortStatus());
        }
        if (loan.getLoanStatus().equals(LoanStatus.DEFERRED)){
            log.info("Loanee is already deferred");
            throw new LoanException("Loanee is already deferred");
        }
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        if(programDurationIsStillWithinFirstQuarter(cohort, program)){
            log.info("Program duration is not within first quarter");
            throw new LoanException(LoaneeMessages.LOANEE_CANNOT_DEFER_LOAN.getMessage());
        }
        loanee.setDeferredDateAndTime(LocalDateTime.now());
        loanee.setDeferReason(reasonForDeferral);
        loanee.setDeferralRequested(true);
        loaneeOutputPort.save(loanee);

        if (loanee.isDeferralRequested() && loanee.isDeferralApproved()){
            loan.setLoanStatus(LoanStatus.DEFERRED);
            loanOutputPort.save(loan);
        }

        asynchronousNotificationOutputPort.sendDeferralNotificationToEmployee(loanee, loan.getId(), NotificationFlag.LOAN_DEFERRAL);
        return "Deferral request sent";
    }

    @Override
    public String resumeProgram(String loanId, String cohortId, String userId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        Loan loan =
                loanOutputPort.findLoanById(loanId);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        if (!userId.equals(loanee.getUserIdentity().getId())) {
            throw new LoanException("Access denied: A loanee cannot resume program on behalf of another loanee");
        }
        Cohort cohort = cohortOutputPort.findCohortById(cohortId);
        if (!loan.getLoanStatus().equals(LoanStatus.DEFERRED)){
            throw new LoanException("The action is for a loanee that deferred");
        }
        if (!cohort.getCohortStatus().equals(CohortStatus.CURRENT)){
            throw new LoanException("Loanee can only resume to a current cohort. Selected cohort is "+ cohort.getCohortStatus());
        }
        loan.setLoanStatus(LoanStatus.PERFORMING);
        loanOutputPort.save(loan);
        return "Successfully resumed";
    }

    @Override
    public String indicateDeferredLoanee(String actorId, String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(actorId,UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(loaneeId,LoaneeMessages.INVALID_LOANEE_ID.getMessage());

        UserIdentity userIdentity = identityOutputPort.findById(actorId);
        Optional<OrganizationEmployeeIdentity> organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId());

        Loanee loanee = loaneeOutputPort.findLoaneeById(loaneeId);

        boolean cohortExistInOrganization =
                loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(loanee.getId(),organizationEmployeeIdentity.get().getOrganization());
        if (!cohortExistInOrganization) {
            throw new LoanException(LoaneeMessages.LOANEE_NOT_ASSOCIATE_WITH_ORGANIZATION.getMessage());
        }

        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        if (programDurationIsStillWithinFirstQuarter(cohort, program)){
            log.info("Program duration is not within first quarter");
            throw new LoanException(LoaneeMessages.LOANEE_CANNOT_DEFER_LOAN.getMessage());
        }

        Optional<Loan> loan = findLoaneeLoanAndDeferLoan(loanee);
        sendLoaneeNotification(loan, loanee, userIdentity);
        sendPortfolioManagersNotification(loan, userIdentity);

        return "Loanee has been Deferred";
    }

    private Optional<Loan> findLoaneeLoanAndDeferLoan(Loanee loanee) throws MeedlException {
        Optional<Loan> loan = loanOutputPort.findLoanByLoanOfferId(loanee.getId());
        if (loan.isEmpty()){
            throw new LoanException(LoanMessages.LOANEE_LOAN_NOT_FOUND.getMessage());
        }
        if (loan.get().getLoanStatus().equals(LoanStatus.DEFERRED)){
            throw new LoanException("Loan already deferred");
        }
        loanee.setDeferralApproved(true);
        loaneeOutputPort.save(loanee);
        if (loanee.isDeferralRequested() && loanee.isDeferralApproved()){
            loan.get().setLoanStatus(LoanStatus.DEFERRED);
        }
        loanOutputPort.save(loan.get());
        return loan;
    }

    private void sendLoaneeNotification(Optional<Loan> loan, Loanee loanee, UserIdentity userIdentity) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .contentId(loan.get().getId())
                .user(loanee.getUserIdentity())
                .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                .senderMail(userIdentity.getEmail())
                .notificationFlag(NotificationFlag.LOAN_DEFERRAL)
                .contentDetail(MeedlNotificationMessages.LOAN_DEFERRAL_LOANEE.getMessage())
                .title(LOAN_DEFERRAL.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        meedlNotificationOutputPort.save(meedlNotification);
    }

    private void sendPortfolioManagersNotification(Optional<Loan> loan, UserIdentity userIdentity) throws MeedlException {
        List<UserIdentity> portfolioManagers =
                userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);

        MeedlNotification portfolioManagerNotification =
                MeedlNotification.builder()
                        .contentId(loan.get().getId())
                        .notificationFlag(NotificationFlag.LOAN_DEFERRAL)
                        .title(LOAN_DEFERRAL.getMessage())
                        .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                        .contentDetail(MeedlNotificationMessages.LOAN_DEFERRAL_PORTFOLIO_MANAGER.getMessage())
                        .senderMail(userIdentity.getEmail())
                        .timestamp(LocalDateTime.now())
                        .build();
        for (UserIdentity portfolioManager : portfolioManagers) {
            portfolioManagerNotification.setUser(portfolioManager);
            meedlNotificationOutputPort.save(portfolioManagerNotification);
        }
    }

    @Override
    public String indicateDropOutLoanee(String userId, String loanId) throws MeedlException {
        MeedlValidator.validateUUID(userId,UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());

        UserIdentity userIdentity = identityOutputPort.findById(userId);
        Optional<OrganizationEmployeeIdentity> organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId());

        Loan loan = loanOutputPort.findLoanById(loanId);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        boolean cohortExistInOrganization =
                loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(loanee.getId(),organizationEmployeeIdentity.get().getOrganization());
        if (! cohortExistInOrganization) {
            throw new LoanException(LoaneeMessages.LOANEE_NOT_ASSOCIATE_WITH_ORGANIZATION.getMessage());
        }

        loanee.setDropoutApproved(true);
        loaneeOutputPort.save(loanee);
        if (loanee.isDropoutRequested() && loanee.isDropoutApproved()){
            loan.setLoanStatus(LoanStatus.DROPOUT);
            loanOutputPort.save(loan);
        }

        sendLoaneeDropOutNotification(loanee, userIdentity);
        sendPortfolioManagerDropOutNotification(loanee, userIdentity);
        return "Loanee has been dropped out";
    }

    @Override
    public String dropOutFromCohort(String userId, String loanId, String reasonForDropout) throws MeedlException {
        MeedlValidator.validateUUID(loanId,LoanMessages.INVALID_LOAN_ID.getMessage());
        MeedlValidator.validateUUID(userId, IdentityMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateObjectInstance(reasonForDropout,"Reason for drop out cannot be empty");

        Loan loan = loanOutputPort.findLoanById(loanId);
        Loanee loanee = loaneeOutputPort.findLoaneeById(loan.getLoaneeId());
        if (!loanee.getUserIdentity().getId().equals(userId)){
            log.info("Loanee cannot dropout on behalf of another loanee");
            throw new LoanException("Loanee cannot dropout on behalf of another loanee");
        }
        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        Program program = programOutputPort.findProgramById(cohort.getProgramId());

        if(programDurationIsStillWithinFirstQuarter(cohort, program)){
            log.info("Program duration is not within the first quarter");
            throw new LoanException(LoaneeMessages.LOANEE_CANNOT_DROP_FROM_COHORT.getMessage());
        }
        log.info("------------------> loanee status -----> {}", loanee);
        loanee.setDropoutRequested(true);
        loanee.setReasonForDropout(reasonForDropout);
        Loanee savedLoanee = loaneeOutputPort.save(loanee);
        log.info("------------------> Check loanee status -----> {}", savedLoanee);

        if (loanee.isDropoutRequested() && loanee.isDropoutApproved()){
            loan.setLoanStatus(LoanStatus.DROPOUT);
            loanOutputPort.save(loan);
        }
        notifyOrganizationAdmin(program, loanee);
        sendPortfolioManagerDropOutNotification(loanee,loanee.getUserIdentity());
        return "Dropout request sent";
    }

    private void notifyOrganizationAdmin(Program program, Loanee loanee) throws MeedlException {
        List<OrganizationEmployeeIdentity> organizationEmployees =
                organizationEmployeeIdentityOutputPort.findAllEmployeesInOrganizationByOrganizationIdAndRole(
                        program.getOrganizationId(),IdentityRole.ORGANIZATION_ADMIN);
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .contentId(loanee.getId())
                .senderFullName(loanee.getUserIdentity().getFirstName()+" "+ loanee.getUserIdentity().getLastName())
                .senderMail(loanee.getUserIdentity().getEmail())
                .notificationFlag(NotificationFlag.DROP_OUT)
                .contentDetail(MeedlNotificationMessages.DROP_OUT_BY_LOANEE.getMessage())
                .title(DROP_OUT.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        for (OrganizationEmployeeIdentity organizationEmployee : organizationEmployees) {
            meedlNotification.setUser(organizationEmployee.getMeedlUser());
            meedlNotificationOutputPort.save(meedlNotification);
        }
    }

    private boolean programDurationIsStillWithinFirstQuarter(Cohort cohort, Program program) {
        LocalDate cohortEndDate = cohort.getStartDate().plusMonths(program.getDuration());
        long totalDays = ChronoUnit.DAYS.between(cohort.getStartDate(), cohortEndDate);
        long quarterDays = totalDays / 4;
        LocalDate firstQuarterEnd = cohort.getStartDate().plusDays(quarterDays);
        return firstQuarterEnd.isBefore(LocalDate.now());
    }

    private static void checkIfLoaneeExistInCohort(boolean existInCohort) throws LoanException {
        if (!existInCohort) {
            throw new LoanException(LoaneeMessages.LOANEE_DOES_NOT_EXIST_IN_COHORT.getMessage());
        }
    }

    @Override
    public String archiveOrUnArchiveByIds(String cohortId, List<String> loaneeIds, LoaneeStatus loaneeStatus) throws MeedlException {
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());
        if (loaneeIds.isEmpty()){
            throw new MeedlException(LoaneeMessages.LOANEES_ID_CANNOT_BE_EMPTY.getMessage());
        }
        for (String loaneeId : loaneeIds) {
            MeedlValidator.validateUUID(loaneeId,UserMessages.INVALID_USER_ID.getMessage());
        }
        cohortLoaneeOutputPort.archiveOrUnArchiveByIds(cohortId,loaneeIds,loaneeStatus);
        if (loaneeIds.size() == 1) {
            return "Loanee has been "+loaneeStatus.name();
        }else {
            return "Loanees has been "+loaneeStatus.name();
        }
    }
    @Override
    public void updateLoaneeStatus(Loanee loanee) {
        if(ObjectUtils.isNotEmpty(loanee) && ObjectUtils.isNotEmpty(loanee.getUserIdentity())
                && ObjectUtils.isNotEmpty(loanee.getUserIdentity().getRole())
                && loanee.getUserIdentity().getRole() == IdentityRole.LOANEE){
            try {
                Loanee foundLoanee = loaneeOutputPort.findByUserId(loanee.getUserIdentity().getId())
                        .orElseThrow(()-> new MeedlException("Loanee not found to activate."));
                foundLoanee.setActivationStatus(ActivationStatus.ACTIVE);
                loaneeOutputPort.save(foundLoanee);
            } catch (MeedlException e) {
                log.error("Failed to save loanee when attempting to update Loanee status for user with id {}", loanee.getUserIdentity().getId(), e);
            }
        }
    }

    @Override
    public CohortLoanee viewLoaneeDetailInCohort(String cohortId, String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());
        return cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loaneeId,cohortId);
    }

    @Override
    public Page<LoaneeLoanAggregate> viewAllLoanee(String actorId,int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        if (userIdentity.getRole().isMeedlRole()) {
            return loaneeLoanAggregateOutputPort.findAllLoanAggregate(pageSize, pageNumber);
        }else {
            OrganizationEmployeeIdentity organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId())
                            .orElseThrow(() -> new LoanException("Organization Employee identity not found."));
            return loaneeLoanAggregateOutputPort.findAllLoanAggregateByOrganizationId(organizationEmployeeIdentity.getOrganization(),
                    pageSize, pageNumber);
        }
    }

    @Override
    public Page<LoaneeLoanAggregate> searchLoanAggregate(Loanee loanee,int pageSize, int pageNumber) throws MeedlException  {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanee.getId());
        if(userIdentity.getRole().isMeedlRole()) {
            return loaneeLoanAggregateOutputPort.searchLoanAggregate(loanee.getLoaneeName(), pageSize, pageNumber);
        }else {
            OrganizationEmployeeIdentity organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId())
                            .orElseThrow(() -> new LoanException("Organization Employee identity not found."));
            loanee.setOrganizationId(organizationEmployeeIdentity.getOrganization());
            return loaneeLoanAggregateOutputPort.searchLoanAggregateByOrganizationId(loanee, pageSize, pageNumber);
        }
    }

    private void sendPortfolioManagerDropOutNotification(Loanee loanee, UserIdentity userIdentity) throws MeedlException {
        List<UserIdentity> portfolioManagers =
                userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);

        MeedlNotification portfolioManagerNotification =
                MeedlNotification.builder()
                        .contentId(loanee.getId())
                        .notificationFlag(NotificationFlag.DROP_OUT)
                        .title(DROP_OUT.getMessage())
                        .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                        .contentDetail(MeedlNotificationMessages.DROP_OUT_PORTFOLIO_MANAGER.getMessage())
                        .senderMail(userIdentity.getEmail())
                        .timestamp(LocalDateTime.now())
                        .build();
        for (UserIdentity portfolioManager : portfolioManagers) {
            portfolioManagerNotification.setUser(portfolioManager);
            meedlNotificationOutputPort.save(portfolioManagerNotification);
        }
    }

    private void sendLoaneeDropOutNotification(Loanee loanee, UserIdentity userIdentity) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .contentId(loanee.getId())
                .user(loanee.getUserIdentity())
                .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                .senderMail(userIdentity.getEmail())
                .notificationFlag(NotificationFlag.DROP_OUT)
                .contentDetail(MeedlNotificationMessages.DROP_OUT_LOANEE.getMessage())
                .title(DROP_OUT.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        meedlNotificationOutputPort.save(meedlNotification);
    }
}

