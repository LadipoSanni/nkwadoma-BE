package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class LoaneeService implements LoaneeUseCase {



    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final UserIdentityOutputPort identityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;


    @Override
    public Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        loanee.validate();
        checkIfLoaneeWithEmailExist(loanee);
        String cohortId = loanee.getCohortId();
        Cohort cohort = cohortOutputPort.findCohort(cohortId);
        checkIfCohortTuitionDetailsHaveBeenUpdated(cohort);
        checkIfInitialDepositIsNotGreaterThanTotalCohortFee(loanee, cohort);
        checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(loanee, cohort);
        BigDecimal totalLoanBreakDown = getTotalLoanBreakdown(loanee);
        calculateAmountRequested(loanee, totalLoanBreakDown, cohort);
        loanee.setCreatedAt(LocalDateTime.now());
        List<LoanBreakdown> loanBreakdowns = loanBreakdownOutputPort.saveAll(loanee.getLoaneeLoanDetail().getLoanBreakdown());
        saveLoaneeLoanDetails(loanee, loanBreakdowns);
        loanee.getUserIdentity().setRole(IdentityRole.LOANEE);
        loanee = createLoaneeAccount(loanee);
        cohort.setNumberOfLoanees(cohort.getNumberOfLoanees() + 1);
        cohortOutputPort.save(cohort);
        return loanee;
    }

    private void saveLoaneeLoanDetails(Loanee loanee, List<LoanBreakdown> loanBreakdowns) {
        LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail.setInitialDeposit(loanee.getLoaneeLoanDetail().getInitialDeposit());
        loaneeLoanDetail.setAmountRequested(loanee.getLoaneeLoanDetail().getAmountRequested());
        loaneeLoanDetail.setLoanBreakdown(loanBreakdowns);
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
    }

    private void checkIfLoaneeWithEmailExist(Loanee loanee) throws MeedlException {
        Loanee existingLoanee = loaneeOutputPort.findByLoaneeEmail(loanee.getUserIdentity().getEmail());
        if (ObjectUtils.isNotEmpty(existingLoanee)) {
            throw new LoaneeException(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage());
        }
    }

    private static void calculateAmountRequested(Loanee loanee, BigDecimal totalLoanBreakDown, Cohort cohort) {
        loanee.getLoaneeLoanDetail().
                setAmountRequested(totalLoanBreakDown.add(cohort.getTuitionAmount()).
                        subtract(loanee.getLoaneeLoanDetail().getInitialDeposit()));
    }

    private static BigDecimal getTotalLoanBreakdown(Loanee loanee) {
        return loanee.getLoaneeLoanDetail().getLoanBreakdown().stream()
                .map(LoanBreakdown::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Loanee createLoaneeAccount(Loanee loanee) throws MeedlException {
        Optional<UserIdentity> foundUserIdentity = identityManagerOutputPort.getUserByEmail(loanee.getUserIdentity().getEmail());
        if (foundUserIdentity.isPresent()){
            throw new IdentityException(IdentityMessages.USER_IDENTITY_ALREADY_EXISTS.getMessage());
        }
        UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
        userIdentity.setCreatedAt(String.valueOf(loanee.getCreatedAt()));
        userIdentity = identityOutputPort.save(userIdentity);
        loanee.setUserIdentity(userIdentity);
        loanee = loaneeOutputPort.save(loanee);
        return loanee;
    }

    private static void checkIfAmountRequestedIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws CohortException {
        if (loanee.getLoaneeLoanDetail().getAmountRequested().compareTo(cohort.getTotalCohortFee()) > 0){
            throw new CohortException(CohortMessages.AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }

    private static void checkIfInitialDepositIsNotGreaterThanTotalCohortFee(Loanee loanee, Cohort cohort) throws CohortException {
        if (loanee.getLoaneeLoanDetail().getInitialDeposit().compareTo(cohort.getTotalCohortFee()) > 0 ){
            throw new CohortException(CohortMessages.INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE.getMessage());
        }
    }

    private static void checkIfCohortTuitionDetailsHaveBeenUpdated(Cohort cohort) throws CohortException {
        if (ObjectUtils.isEmpty(cohort.getTuitionAmount())){
            throw new CohortException(CohortMessages.COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED.getMessage());
        }
    }


}
