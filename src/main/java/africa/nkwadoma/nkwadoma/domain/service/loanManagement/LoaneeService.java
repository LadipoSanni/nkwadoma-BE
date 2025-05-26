package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages.DROP_OUT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages.LOAN_DEFERRAL;

@Slf4j
@AllArgsConstructor
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

    @Override
    public Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException {
        log.info("Validating loanee before adding");
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        loanee.validate();
        loanee.getLoaneeLoanDetail().validate();
        checkIfLoaneeWithEmailExist(loanee);
        String cohortId = loanee.getCohortId();
        Cohort cohort = cohortOutputPort.findCohort(cohortId);
        checkIfCohortTuitionDetailsHaveBeenUpdated(cohort);
        checkIfInitialDepositIsNotGreaterThanTotalCohortFee(loanee, cohort);
        BigDecimal totalLoanBreakDown = getTotalLoanBreakdown(loanee);
        calculateAmountRequested(loanee, totalLoanBreakDown, cohort);
        checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(loanee, cohort);
        loanee.setCreatedAt(LocalDateTime.now());
        LoaneeLoanDetail loaneeLoanDetail = saveLoaneeLoanDetails(loanee.getLoaneeLoanDetail());
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanee.getUserIdentity().setRole(IdentityRole.LOANEE);
        loanee.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
        List<LoaneeLoanBreakdown> loanBreakdowns = loanee.getLoanBreakdowns();
        loanee = createLoaneeAccount(loanee);
        loanBreakdowns = loaneeLoanBreakDownOutputPort.saveAll(loanBreakdowns,loanee);
        loanee.setLoanBreakdowns(loanBreakdowns);
        cohort.setNumberOfLoanees(cohort.getNumberOfLoanees() + 1);
        increaseNumberOfLoaneesInProgram(cohort);
        increaseNumberOfLoaneesInOrganization(cohort);
        cohortOutputPort.save(cohort);
        return loanee;
    }

    private void increaseNumberOfLoaneesInOrganization(Cohort cohort) throws MeedlException {
        OrganizationIdentity organizationIdentity =
                organizationIdentityOutputPort.findById(cohort.getOrganizationId());
        organizationIdentity.setNumberOfLoanees(organizationIdentity.getNumberOfLoanees() + 1);
        organizationIdentity.setOrganizationEmployees(
                organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId()));
        organizationIdentityOutputPort.save(organizationIdentity);
        log.info("Total number of loanees in an organization has been increased to : {}, in organization with id : {}", organizationIdentity.getNumberOfLoanees(), organizationIdentity.getId());
    }
    private void increaseNumberOfLoaneesInProgram(Cohort cohort) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
//        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId());
//        program.setOrganizationIdentity(organizationIdentity);
        program.setNumberOfLoanees(program.getNumberOfLoanees() + 1);
        program = programOutputPort.saveProgram(program);
        log.info("Total number of loanees in a program has been increased to : {}, in program with id : {}", program.getNumberOfLoanees(), program.getId());
    }

    @Override
    public Loanee viewLoaneeDetails(String id, String userId) throws MeedlException {
        MeedlValidator.validateUUID(id, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Loanee loanee = loaneeOutputPort.findLoaneeById(id);
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        log.info("----------> User identity ---------> {} userId {}", userIdentity, userId);
        if (userIdentity.getRole().equals(IdentityRole.LOANEE)){
            Optional<Loanee> foundLoanee = loaneeOutputPort
                    .findByUserId(userId);
            if (foundLoanee.isPresent() && !foundLoanee.get().getId().equals(loanee.getId())) {
                throw new MeedlException("Access denied: You can only view your own loan details.");
            }
        }
        log.info("loanee found successfully. Loanee with id {}", id);
        return updateLoaneeCreditScore(loanee);
    }

    private Loanee updateLoaneeCreditScore(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity(), UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());

        if (loanee.getUserIdentity().getBvn() != null) {
            if (ObjectUtils.isEmpty(loanee.getCreditScoreUpdatedAt()) ||
                    creditScoreIsAboveOrEqualOneMonth(loanee)) {
                loanee = updateCreditScore(loanee);
            }
        }

        log.info("Credit score for loanee with id {} has already been updated within the last month", loanee.getId());

        Cohort cohort = cohortOutputPort.findCohort(loanee.getCohortId());
        loanee.setCohortName(cohort.getName());
        loanee.setCohortStartDate(cohort.getStartDate());
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        loanee.setProgramName(program.getName());

//        Loan loan = loanOutputPort.findLoanById(loanee.getLoaneeLoanDetail().getId());
//        LoanProduct loanProduct = loanProductOutputPort.findById(loanee.);
        return loanee;

    }

    private Loanee updateCreditScore(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity().getBvn(), UserMessages.BVN_CANNOT_BE_EMPTY.getMessage());
        log.info("Updating credit score, for loanee with id {}. Last date updated was {}.", loanee.getId(), loanee.getCreditScoreUpdatedAt());
        log.info("Encrypted Loanee BVN: {}", loanee.getUserIdentity().getBvn());
        String decryptedBVN = tokenUtils.decryptAES(loanee.getUserIdentity().getBvn(), "Error processing identity verification");
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
    public Page<Loanee> viewAllLoaneeInCohort(String cohortId, int pageSize, int pageNumber, String sortBy) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        return loaneeOutputPort.findAllLoaneeByCohortId(cohortId, pageSize, pageNumber, sortBy);
    }

    private LoaneeLoanDetail saveLoaneeLoanDetails(LoaneeLoanDetail loaneeLoanDetail) throws MeedlException {
        loaneeLoanDetail.validate();
        return loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
    }

    @Override
    public LoanReferral referLoanee(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanee.getId(), LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(loanee.getCohortId(), CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateObjectInstance(loanee.getOnboardingMode(), LoaneeMessages.INVALID_ONBOARDING_MODE.getMessage());

        OrganizationIdentity organizationIdentity = null;
        if (loanee.getOnboardingMode().equals(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)){
            organizationIdentity = getLoaneeOrganization(loanee.getCohortId());
        }else {
            organizationIdentity = getLoaneeOrganization(loanee);
        }
        checkIfLoaneeHasBeenReferredInTheSameCohort(loanee);
        return referLoanee(loanee, organizationIdentity);
    }

    private LoanReferral referLoanee(Loanee loanee, OrganizationIdentity organizationIdentity) throws MeedlException {
        loanee.setReferredBy(organizationIdentity.getName());
        updateLoaneeReferralDetail(loanee);
        log.info("referred by {}", loanee.getReferredBy());
        LoanReferral loanReferral = loanReferralOutputPort.createLoanReferral(loanee);
        Cohort cohort = cohortOutputPort.findCohort(loanee.getCohortId());
        cohort.setNumberOfReferredLoanee(cohort.getNumberOfReferredLoanee() + 1);
        cohortOutputPort.save(cohort);
        Optional<LoanMetrics> loanMetrics = updateLoanMetrics(organizationIdentity);
        log.info("Loan metrics saved: {}", loanMetrics);
        List<LoaneeLoanBreakdown> loanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByLoaneeId(loanee.getId());
        loanReferral.getLoanee().setLoanBreakdowns(loanBreakdowns);
        log.info("loan referral org == {}", loanReferral.getLoanee().getReferredBy());
        return loanReferral;
    }

    private void updateLoaneeReferralDetail(Loanee loanee) throws MeedlException {
        loanee.setLoaneeStatus(LoaneeStatus.REFERRED);
        loanee.setReferralDateTime(LocalDateTime.now());
        loaneeOutputPort.save(loanee);
        log.info("saved loanee referred by {}", loanee.getReferredBy());
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

    private void checkIfLoaneeHasBeenReferredInTheSameCohort(Loanee loanee) throws MeedlException {
        checkLoaneeStatus(loanee);
        LoanReferral loanReferral =
                loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(loanee.getId(),loanee.getCohortId());
        if (ObjectUtils.isNotEmpty(loanReferral)) {
            log.error("Loanee has been referred to this cohort before with error: {} ", LoaneeMessages.LOANEE_HAS_BEEN_REFERRED_BEFORE.getMessage());
            throw new LoaneeException(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED_BEFORE.getMessage());
        }
        log.info("Loanee has not been referred to this cohort before.");
    }
    @Override
    @Async
    public void notifyLoanReferralActors(List<Loanee> loanees){
        loanees.forEach(loanee -> {
            try {
                refer(loanee);
                notifyAllPortfolioManager();
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        });
    }
    private void notifyAllPortfolioManager() throws MeedlException {
        for (UserIdentity userIdentity : identityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            notifyPortfolioManager(userIdentity);
        }
    }
    private void notifyPortfolioManager(UserIdentity userIdentity) throws MeedlException {
        loaneeEmailUsecase.sendLoaneeHasBeenReferEmail(userIdentity);
    }

    private void refer(Loanee loanee) throws MeedlException {
        loaneeEmailUsecase.referLoaneeEmail(loanee);
    }

    @Override
    public List<Loanee> searchForLoaneeInCohort(String name, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateDataElement(name, MeedlMessages.INVALID_SEARCH_PARAMETER.getMessage());
        return loaneeOutputPort.searchForLoaneeInCohort(name,cohortId);
    }

    private void checkLoaneeStatus(Loanee loanee) throws MeedlException {
//        Loanee loanee;
//        List<Loanee> loanees = loaneeOutputPort.findAllLoaneesByCohortId(cohort.getId());
//        loanee = loanees.stream().filter(eachLoanee -> eachLoanee.getId().equals(loaneeId)).findFirst()
//                .orElseThrow(()-> new LoaneeException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage()));
        if (loanee.getLoaneeStatus().equals(LoaneeStatus.REFERRED)){
            throw new LoaneeException(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED.getMessage());
        }else if (!loanee.getLoaneeStatus().equals(LoaneeStatus.ADDED)){
            throw new LoaneeException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage());
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
    private void checkIfLoaneeWithEmailExist(Loanee loanee) throws MeedlException {
        Loanee existingLoanee = loaneeOutputPort.findByLoaneeEmail(loanee.getUserIdentity().getEmail());
        if (ObjectUtils.isNotEmpty(existingLoanee)) {
            log.error("{}. {}", LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage(), loanee.getUserIdentity().getEmail());
            throw new LoaneeException(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage());
        }
        log.info("Successfully confirmed user does not previously exist. {}",loanee.getUserIdentity().getEmail());
    }

    private static void calculateAmountRequested(Loanee loanee, BigDecimal totalLoanBreakDown, Cohort cohort) throws LoaneeException {
        log.info("Calculating amount requested for loanee {}", loanee.getUserIdentity().getEmail());
        loanee.getLoaneeLoanDetail().
                setAmountRequested(totalLoanBreakDown.add(cohort.getTuitionAmount()).
                        subtract(loanee.getLoaneeLoanDetail().getInitialDeposit()));
        if (loanee.getLoaneeLoanDetail().getAmountRequested().compareTo(BigDecimal.ZERO)  <= 0){
            log.info("Loanee amount request is zero or negative {}", loanee.getLoaneeLoanDetail().getAmountRequested());
            throw new LoaneeException(LoaneeMessages.LOANEE_WITH_ZERO_OR_NEGATIVE_AMOUNT_REQUEST_CANNOT_BE_ADDED_TO_COHORT.getMessage());
        }
    }

    private static BigDecimal getTotalLoanBreakdown(Loanee loanee) throws MeedlException {
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
            throw new IdentityException(IdentityMessages.USER_IDENTITY_ALREADY_EXISTS.getMessage());
        }
        UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
        userIdentity.setCreatedAt(loanee.getCreatedAt());
        userIdentity = identityOutputPort.save(userIdentity);
        log.info("User identity saved successfully with id {}. Now proceeding to save loanee ", userIdentity.getId());
        loanee.setUserIdentity(userIdentity);
        loanee.setLoaneeStatus(LoaneeStatus.ADDED);
        loanee = loaneeOutputPort.save(loanee);
        log.info("Loanee added successfully to a cohort. loanee id : {}. ",loanee.getId());
        return loanee;
    }

    private static void checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws CohortException {
        if (loanee.getLoaneeLoanDetail().getAmountRequested().compareTo(cohort.getTotalCohortFee()) > 0) {
            log.info("{}. Cohort id: {}", CohortMessages.AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage(), cohort.getId());
            throw new CohortException(CohortMessages.AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }

    private static void checkIfInitialDepositIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws CohortException {
        if (loanee.getLoaneeLoanDetail().getInitialDeposit().compareTo(cohort.getTotalCohortFee()) > 0) {
            log.info("{}. Cohort id: {}",CohortMessages.INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage(), cohort.getId());
            throw new CohortException(CohortMessages.INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }

    private static void checkIfCohortTuitionDetailsHaveBeenUpdated(Cohort cohort) throws CohortException {
        if (ObjectUtils.isEmpty(cohort.getTuitionAmount())) {
            log.info("Cohort does not have any cohort tuition details. Cohort id: {}", cohort.getId());
            throw new CohortException(CohortMessages.COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED.getMessage());
        }
    }


    @Override
    public Page<Loanee> viewAllLoaneeThatBenefitedFromLoanProduct(String loanProductId,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId,"Loan product id cannot be empty");
        LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
        return loaneeOutputPort.findAllLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),pageSize,pageNumber);
    }

    @Override
    public Page<Loanee> searchLoaneeThatBenefitedFromLoanProduct(String loanProductId,String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId,"Loan product id cannot be empty");
        LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
        return loaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),name,pageSize,pageNumber);
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
        if (! cohortExistInOrganization) {
            throw new LoaneeException(LoaneeMessages.LOANEE_NOT_ASSOCIATE_WITH_ORGANIZATION.getMessage());
        }

        Optional<Loan> loan = findLoaneeLoanAndDeferLoan(loanee);

        sendLoaneeNotification(loan, loanee, userIdentity);

        sendPortfolioManagersNotification(loan, userIdentity);

        return "Loanee has been Deferred";
    }

    private Optional<Loan> findLoaneeLoanAndDeferLoan(Loanee loanee) throws MeedlException {
        Optional<Loan> loan = loanOutputPort.viewLoanByLoaneeId(loanee.getId());
        if (loan.isEmpty()){
            throw new LoanException(LoanMessages.LOANEE_LOAN_NOT_FOUND.getMessage());
        }

        loan.get().setLoanStatus(LoanStatus.DEFERRED);
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
    public String indicateDropOutLoanee(String actorId, String loaneeId) throws MeedlException {

        MeedlValidator.validateUUID(actorId,UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(loaneeId,LoaneeMessages.INVALID_LOANEE_ID.getMessage());

        UserIdentity userIdentity = identityOutputPort.findById(actorId);
        Optional<OrganizationEmployeeIdentity> organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId());

        Loanee loanee = loaneeOutputPort.findLoaneeById(loaneeId);

        boolean cohortExistInOrganization =
                loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(loanee.getId(),organizationEmployeeIdentity.get().getOrganization());
        if (! cohortExistInOrganization) {
            throw new LoaneeException(LoaneeMessages.LOANEE_NOT_ASSOCIATE_WITH_ORGANIZATION.getMessage());
        }

        loanee.setLoaneeStatus(LoaneeStatus.DROPOUT);
        loaneeOutputPort.save(loanee);
        sendLoaneeDropOutNotification(loanee, userIdentity);

        sendPortfolioManagerDropOutNotification(loanee, userIdentity);

        return "Loanee has been dropped out";
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

