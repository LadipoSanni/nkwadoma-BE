package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final SendLoaneeEmailUsecase sendLoaneeEmailUsecase;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final CreditRegistryOutputPort creditRegistryOutputPort;
    private final LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;


    @Override
    public Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException {
        log.info("Validating loanee before adding");
        MeedlValidator.validateObjectInstance(loanee);
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
        program.setNumberOfLoanees(program.getNumberOfLoanees() + 1);
        program = programOutputPort.saveProgram(program);
        log.info("Total number of loanees in a program has been increased to : {}, in program with id : {}", program.getNumberOfLoanees(), program.getId());
    }
    @Override
    public Loanee viewLoaneeDetails(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Loanee loanee = loaneeOutputPort.findLoaneeById(id);
        return updateLoaneeCreditScore(loanee);
    }

    private Loanee updateLoaneeCreditScore(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        if (loanee.getCreditScoreUpdatedAt() == null){
            loanee.setCreditScore(creditRegistryOutputPort.getCreditScoreWithBvn(loanee.getUserIdentity().getBvn()));
            loanee.setCreditScoreUpdatedAt(LocalDateTime.now());
        }
        creditRegistryOutputPort.getCreditScoreWithRegistryId("");
    }

    @Override
    public Page<Loanee> viewAllLoaneeInCohort(String cohortId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        return loaneeOutputPort.findAllLoaneeByCohortId(cohortId, pageSize, pageNumber);
    }

    private LoaneeLoanDetail saveLoaneeLoanDetails(LoaneeLoanDetail loaneeLoanDetail) throws MeedlException {
        loaneeLoanDetail.validate();
        return loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
    }

    @Override
    public LoanReferral referLoanee(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Loanee loanee = loaneeOutputPort.findLoaneeById(loaneeId);
        Cohort cohort = cohortOutputPort.findCohort(loanee.getCohortId());
        List<LoaneeLoanBreakdown> loanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllByLoaneeId(loaneeId);
        loanee = getLoaneeFromCohort(cohort, loaneeId);
        loanee.setLoaneeStatus(LoaneeStatus.REFERRED);
        loanee.setReferralDateTime(LocalDateTime.now());
        OrganizationEmployeeIdentity organizationEmployeeIdentity = getOrganizationEmployeeIdentity(loanee);
        notifyPortfolioManager(organizationEmployeeIdentity.getMeedlUser());
        loaneeOutputPort.save(loanee);
        cohort.setNumberOfReferredLoanee(cohort.getNumberOfReferredLoanee() + 1);
        cohortOutputPort.save(cohort);
        LoanReferral loanReferral = loanReferralOutputPort.createLoanReferral(loanee);
        refer(loanee,loanReferral.getId());
        loanReferral.getLoanee().setLoanBreakdowns(loanBreakdowns);
        return  loanReferral;
    }

    @Override
    public List<Loanee> searchForLoaneeInCohort(String name, String cohortId) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Loanee name is required.");
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        return loaneeOutputPort.searchForLoaneeInCohort(name,cohortId);
    }

    private Loanee getLoaneeFromCohort(Cohort cohort, String loaneeId) throws MeedlException {
        Loanee loanee;
        List<Loanee> loanees = loaneeOutputPort.findAllLoaneesByCohortId(cohort.getId());
        loanee = loanees.stream().filter(eachLoanee -> eachLoanee.getId().equals(loaneeId)).findFirst()
                .orElseThrow(()-> new LoaneeException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage()));
        if (loanee.getLoaneeStatus().equals(LoaneeStatus.REFERRED)){
            throw new LoaneeException(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED.getMessage());
        }else if (!loanee.getLoaneeStatus().equals(LoaneeStatus.ADDED)){
            throw new LoaneeException(LoaneeMessages.LOANEE_MUST_BE_ADDED_TO_COHORT.getMessage());
        }
        return loanee;
    }

    private OrganizationEmployeeIdentity getOrganizationEmployeeIdentity(Loanee loanee) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByEmployeeId(loanee.getUserIdentity().getCreatedBy());
        OrganizationIdentity organizationIdentity =
                organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
        loanee.setReferredBy(organizationIdentity.getName());
        return organizationEmployeeIdentity;
    }

    private void notifyPortfolioManager(UserIdentity userIdentity) throws MeedlException {
        sendLoaneeEmailUsecase.sendLoaneeHasBeenReferEmail(userIdentity);
    }


    private void refer(Loanee loanee,String loanReferralId) throws MeedlException {
        sendLoaneeEmailUsecase.referLoaneeEmail(loanee,loanReferralId);
    }

    private void checkIfLoaneeWithEmailExist(Loanee loanee) throws MeedlException {
        Loanee existingLoanee = loaneeOutputPort.findByLoaneeEmail(loanee.getUserIdentity().getEmail());
        if (ObjectUtils.isNotEmpty(existingLoanee)) {
            log.error("{}. {}", LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage(), loanee.getUserIdentity().getEmail());
            throw new LoaneeException(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage());
        }
        log.info("Successfully confirmed user does not previously exist. {}",loanee.getUserIdentity().getEmail());
    }

    private static void calculateAmountRequested(Loanee loanee, BigDecimal totalLoanBreakDown, Cohort cohort) {
        log.info("Calculating amount requested for loanee {}", loanee.getUserIdentity().getEmail());
        loanee.getLoaneeLoanDetail().
                setAmountRequested(totalLoanBreakDown.add(cohort.getTuitionAmount()).
                        subtract(loanee.getLoaneeLoanDetail().getInitialDeposit()));
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
        userIdentity.setCreatedAt(String.valueOf(loanee.getCreatedAt()));
        userIdentity = identityOutputPort.save(userIdentity);
        log.info("User identity saved successfully with id {}. Now proceeding to save loanee ", userIdentity.getId());
        loanee.setUserIdentity(userIdentity);
        loanee.setFullName(loanee.getUserIdentity().getFirstName().concat(loanee.getUserIdentity().getLastName()));
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



}

