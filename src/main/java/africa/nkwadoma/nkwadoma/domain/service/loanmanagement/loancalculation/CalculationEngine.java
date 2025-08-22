package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAggregateOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DailyInterestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.MonthlyInterestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalculationEngine implements CalculationEngineUseCase {
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private final OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private final DailyInterestOutputPort dailyInterestOutputPort;
    private final MonthlyInterestOutputPort monthlyInterestOutputPort;
    private final JobScheduler jobScheduler;
    private final LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;


    @Override
    public List<RepaymentHistory> sortRepaymentsByDateTimeAscending(List<RepaymentHistory> repayments) throws MeedlException {
        log.info("Started the sorting ");
        if (repayments == null) {
            log.warn("Repayments was null in the sorting method");
            return Collections.emptyList();
        }
        List<RepaymentHistory> mutableRepayments = new ArrayList<>(repayments);
        log.info("The repayment list before sorting {} \n --------------------------------------------    Repayments are not empty and the sorting has started. The number of the repayment is :{}", mutableRepayments, repayments.size());
        validateRepaymentHistoriesBeforeSorting(mutableRepayments);
        mutableRepayments.sort(Comparator.comparing(RepaymentHistory::getPaymentDateTime));
        return mutableRepayments;
    }

    private static void validateRepaymentHistoriesBeforeSorting(List<RepaymentHistory> mutableRepayments) throws MeedlException {
        for (RepaymentHistory repayment : mutableRepayments) {
            if (repayment == null) {
                log.warn("Repayment history cannot be null, before sorting repayment by date.");
                throw new MeedlException(LoanCalculationMessages.REPAYMENT_HISTORY_MUST_BE_PROVIDED.getMessage());
            }
            log.info("Date of this repayment is : {}", repayment.getPaymentDateTime());
            if (repayment.getPaymentDateTime() == null) {
                log.warn("Payment date cannot be null, before sorting repayment by date.");
                throw new MeedlException(LoanCalculationMessages.PAYMENT_DATE_CANNOT_BE_NULL.getMessage());
            }
        }
    }

    @Transactional
    @Override
    public void calculateLoaneeLoanRepaymentHistory(CalculationContext calculationContext) throws MeedlException {
        if (isSkipableCalculation(calculationContext.getRepaymentHistories(), calculationContext.getLoanee())) return;
        calculationContext.setDefaultValues();
        List<RepaymentHistory> previousRepaymentHistory = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(calculationContext.getLoanee().getId(), calculationContext.getCohort().getId());
        LoaneeLoanDetail loaneeLoanDetail = getLoaneeLoanDetail(calculationContext);
        calculationContext.setLoaneeLoanDetail(loaneeLoanDetail);
        deletePreciousInterestHistory(calculationContext);
        log.info("On repayment calculation, the cohort id is {} the loanee id is {}", calculationContext.getCohort().getId(), calculationContext.getLoanee().getId());
        List<RepaymentHistory> allRepayments = combineAndSortRepaymentHistories(calculationContext.getRepaymentHistories(), previousRepaymentHistory);
        allRepayments.forEach(repaymentHistory -> {
            repaymentHistory.setCohort(calculationContext.getCohort());
            repaymentHistory.setLoanee(calculationContext.getLoanee());
        });
        calculationContext.setRepaymentHistories(allRepayments);
        calculationContext.setAsOfDate(LocalDate.now());

        processRepaymentHistoryCalculations(calculationContext);
        finalizeRepaymentHistoryCalculation(allRepayments, previousRepaymentHistory, loaneeLoanDetail);
        updateLoanDetails(calculationContext);
    }

    private void deletePreciousInterestHistory(CalculationContext calculationContext) throws MeedlException {
        log.info("Deleting previously existing monthly and daily interest for this particular loan if any exist. Loanee loan detail id is {}", calculationContext.getLoaneeLoanDetail().getId());
        monthlyInterestOutputPort.deleteAllByLoaneeLoanDetailId(calculationContext.getLoaneeLoanDetail().getId());
        dailyInterestOutputPort.deleteAllByLoaneeLoanDetailId(calculationContext.getLoaneeLoanDetail().getId());

    }

    private void finalizeRepaymentHistoryCalculation(
            List<RepaymentHistory> currentRepayments,
            List<RepaymentHistory> previousRepayments,
            LoaneeLoanDetail loaneeLoanDetail
    ) {
        loaneeLoanDetail.setUpdatedAt(LocalDateTime.now());
        log.info("Loanee loan details in repayment calculation before being saved {}", loaneeLoanDetail);
        LoaneeLoanDetail updatedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        log.info("Loanee loan details updated with  repayment calculations. {}", updatedLoaneeLoanDetail);
        updateLoaneeRepaymentHistory(currentRepayments, previousRepayments);
    }

    private void calculateCohortLoanDetail(CalculationContext calculationContext) throws MeedlException {
        log.info("About to Update Cohort loan detail after repayment ");
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(calculationContext.getCohort().getId());
        log.info("Cohort loan detail found {} \n  ---------------------> previous cohort total amount outstanding {}", cohortLoanDetail, cohortLoanDetail.getOutstandingAmount());

        calculationContext.setCohortLoanDetail(cohortLoanDetail);
        BigDecimal newCohortTotalOutstandingAmount = calculateCohortTotalOutstandingAmount(calculationContext);
        cohortLoanDetail.setOutstandingAmount(newCohortTotalOutstandingAmount);
        log.info("new cohort total outstanding amount {}, \n -------------------------------> Previous cohort total amount repaid {} ", newCohortTotalOutstandingAmount, cohortLoanDetail.getAmountRepaid());

        BigDecimal newCohortTotalAmountRepaid = calculateCohortTotalAmountRepaid(calculationContext);
        cohortLoanDetail.setAmountRepaid(newCohortTotalAmountRepaid);
        log.info("New cohort total amount repaid {} \n --------------------------------> previous cohort interest incurred {} ", newCohortTotalAmountRepaid, cohortLoanDetail.getInterestIncurred());

        BigDecimal newCohortTotalInterestIncurred = calculateCohortTotalInterestIncurred(calculationContext);
        cohortLoanDetail.setInterestIncurred(newCohortTotalInterestIncurred);
        log.info("New Cohort total interest incurred  {}", newCohortTotalInterestIncurred);

        cohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
        log.info("cohort loan details after saving {}", cohortLoanDetail);
    }

    private void updateLoanDetails(CalculationContext calculationContext) throws MeedlException {

        calculateLoaneeLoanAgregate(calculationContext);
        calculateCohortLoanDetail(calculationContext);
        calculateProgramLoanDetail(calculationContext);
        calculateOrganizationLoanDetail(calculationContext);
    }

    private void calculateLoaneeLoanAgregate(CalculationContext calculationContext) throws MeedlException {
        log.info("loanee loan detail before finding Aggregation == {}", calculationContext.getLoaneeLoanDetail());
        log.info("loanee id before calculating aggregate {}", calculationContext.getLoanee().getId());

        BigDecimal currentAmountRepaid = calculationContext.getLoaneeLoanDetail().getAmountRepaid()
                .subtract(calculationContext.getPreviousTotalAmountPaid());

        log.info("current amount repaid == {} total amount repaid == {} previous amount repaid == {}",
                currentAmountRepaid,calculationContext.getPreviousTotalAmountPaid(),
                calculationContext.getLoaneeLoanDetail().getAmountRepaid());

        log.info("previous interest incured ==  {}",calculationContext.getPreviousTotalInterestIncurred());

        log.info("total interest incurred == {}",calculationContext.getLoaneeLoanDetail().getInterestIncurred());

        BigDecimal currentInterest = calculationContext.getLoaneeLoanDetail().getInterestIncurred().subtract(calculationContext.getPreviousTotalInterestIncurred());
        log.info("current interest {}",currentInterest);

        LoaneeLoanAggregate loaneeLoanAggregate =
                loaneeLoanAggregateOutputPort.findByLoaneeLoanAggregateByLoaneeLoanDetailId(calculationContext.getLoaneeLoanDetail().getId());
        log.info("found loanee loan aggregate {}", loaneeLoanAggregate);
        loaneeLoanAggregate.setTotalAmountRepaid(loaneeLoanAggregate.getTotalAmountRepaid().add(currentAmountRepaid));
        loaneeLoanAggregate.setTotalAmountOutstanding(loaneeLoanAggregate.getTotalAmountOutstanding()
                .subtract(currentAmountRepaid).add(currentInterest));
        log.info("About to save aggregate on update made.");
        loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
        log.info("loanee loan aggregate after saving {}", loaneeLoanAggregate);
    }

    private void calculateProgramLoanDetail(CalculationContext calculationContext) throws MeedlException {

        log.info("About to Update Program loan detail after repayment ");
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(calculationContext.getCohortLoanDetail().getCohort().getProgramId());
        log.info("Program loan detail found {} \n  ---------------------> previous program total amount outstanding {}", programLoanDetail, programLoanDetail.getOutstandingAmount());

        calculationContext.setProgramLoanDetail(programLoanDetail);
        BigDecimal newProgramTotalOutstandingAmount = calculateProgramTotalOutstandingAmount(calculationContext);
        programLoanDetail.setOutstandingAmount(newProgramTotalOutstandingAmount);
        log.info("new program total outstanding amount {}, \n -------------------------------> Previous program total amount repaid {} ", newProgramTotalOutstandingAmount, programLoanDetail.getAmountRepaid());

        BigDecimal newProgramTotalAmountRepaid = calculateProgramTotalAmountRepaid(calculationContext);
        programLoanDetail.setAmountRepaid(newProgramTotalAmountRepaid);
        log.info("New program total amount repaid {} \n --------------------------------> previous program interest incurred {} ", newProgramTotalAmountRepaid, programLoanDetail.getInterestIncurred());

        BigDecimal newProgramTotalInterestIncurred = calculateProgramTotalInterestIncurred(calculationContext);
        programLoanDetail.setInterestIncurred(newProgramTotalInterestIncurred);
        log.info("New program total interest incurred  {}", newProgramTotalInterestIncurred);

        programLoanDetail = programLoanDetailOutputPort.save(programLoanDetail);
        log.info("program loan details after saving {}", programLoanDetail);

    }

    private void calculateOrganizationLoanDetail(CalculationContext calculationContext) throws MeedlException {
        log.info("About to Update organization's loan detail after repayment ");
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(calculationContext.getProgramLoanDetail().getProgram().getOrganizationIdentity().getId());
        log.info("Organization's loan detail found before any updates -- {} \n  --------------------->  organization's previous total amount outstanding {}", organizationLoanDetail, organizationLoanDetail.getOutstandingAmount());

        calculationContext.setOrganizationLoanDetail(organizationLoanDetail);
        BigDecimal newOrganizationTotalOutstandingAmount = calculateOrganizationTotalOutstandingAmount(calculationContext);
        organizationLoanDetail.setOutstandingAmount(newOrganizationTotalOutstandingAmount);
        log.info("Organization's new total outstanding amount {}, \n ------------------------------->  Organization previous total amount repaid {} ", newOrganizationTotalOutstandingAmount, organizationLoanDetail.getAmountRepaid());

        BigDecimal newOrganizationTotalAmountRepaid = calculateOrganizationTotalAmountRepaid(calculationContext);
        organizationLoanDetail.setAmountRepaid(newOrganizationTotalAmountRepaid);
        log.info("New organization total amount repaid {} \n --------------------------------> organization's previous interest incurred {} ", newOrganizationTotalAmountRepaid, organizationLoanDetail.getInterestIncurred());

        BigDecimal newOrganizationTotalInterestIncurred = calculateOrganizationTotalInterestIncurred(calculationContext);
        organizationLoanDetail.setInterestIncurred(newOrganizationTotalInterestIncurred);
        log.info("organization's new total interest incurred  {}", newOrganizationTotalInterestIncurred);

        organizationLoanDetail = organizationLoanDetailOutputPort.save(organizationLoanDetail);
        log.info("Organization's loan detail after saving {}", organizationLoanDetail);

    }

    /// Cohort loan detail calculation
    private BigDecimal calculateCohortTotalInterestIncurred(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getInterestIncurred()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getLoaneeLoanDetail().getInterestIncurred());
    }

    private BigDecimal calculateCohortTotalAmountRepaid(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getAmountRepaid()
                .subtract(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private BigDecimal calculateCohortTotalOutstandingAmount(CalculationContext context) {
        log.info("Cohort loan detail outstanding amount {}", context.getCohortLoanDetail().getOutstandingAmount());
        log.info("loanee previous interest incurred is {}", context.getPreviousTotalInterestIncurred());
        return context.getCohortLoanDetail()
                .getOutstandingAmount()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getInterestIncurred())
                .subtract(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    /// Program loan detail calculation
    private BigDecimal calculateProgramTotalInterestIncurred(CalculationContext context) {
        return context.getProgramLoanDetail()
                .getInterestIncurred()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getLoaneeLoanDetail().getInterestIncurred());
    }

    private BigDecimal calculateProgramTotalAmountRepaid(CalculationContext context) {
        return context.getProgramLoanDetail()
                .getAmountRepaid()
                .subtract(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private BigDecimal calculateProgramTotalOutstandingAmount(CalculationContext context) {
        return context.getProgramLoanDetail()
                .getOutstandingAmount()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getInterestIncurred())
                .subtract(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    /// Organization loan detail calculation
    private BigDecimal calculateOrganizationTotalInterestIncurred(CalculationContext context) {
        return context.getOrganizationLoanDetail()
                .getInterestIncurred()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getLoaneeLoanDetail().getInterestIncurred());
    }

    private BigDecimal calculateOrganizationTotalAmountRepaid(CalculationContext context) {
        return context.getOrganizationLoanDetail()
                .getAmountRepaid()
                .subtract(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private BigDecimal calculateOrganizationTotalOutstandingAmount(CalculationContext context) {
        return context.getOrganizationLoanDetail()
                .getOutstandingAmount()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getInterestIncurred())
                .subtract(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private void updateRepaymentMeta(RepaymentHistory repayment, CalculationContext calculationContext) {
        repayment.setCohort(calculationContext.getCohort());
        repayment.setLoanee(calculationContext.getLoanee());
    }

    private boolean isSkipableCalculation(List<RepaymentHistory> repaymentHistories, Loanee loanee) {
        return ObjectUtils.isEmpty(repaymentHistories) || repaymentHistories.isEmpty() || ObjectUtils.isEmpty(loanee);
    }
    private void processRepaymentHistoryCalculations(
            CalculationContext calculationContext
    ) throws MeedlException {
        calculationContext.setStartDate(calculationContext.getLoaneeLoanDetail().getLoanStartDate());

        log.info("Started processing repayment history calculations");
        BigDecimal outstanding = calculationContext.getLoaneeLoanDetail().getAmountReceived();
        BigDecimal monthlyInterestAccrued = BigDecimal.ZERO;
        BigDecimal interestAccruedBeforeRepayment = BigDecimal.ZERO;
        LocalDate currentDate = calculationContext.getStartDate().toLocalDate();
        int repaymentIndex = 0;
        LocalDate asOfDate = calculationContext.getAsOfDate();
        double annualRate = calculationContext.getLoaneeLoanDetail().getInterestRate();

        BigDecimal totalAmountOutstanding = BigDecimal.ZERO;
        BigDecimal totalAmountRepaid = BigDecimal.ZERO;
        BigDecimal totalInterestIncurred = BigDecimal.ZERO;
        log.info("Current date {}, as of date is {}", currentDate, asOfDate);

        while (!currentDate.isAfter(asOfDate)) {
            // daily interest accrual
            BigDecimal dailyInterest = decimalPlaceRoundUp(calculateInterest(annualRate, outstanding, 1));
            calculateAndSaveDailyInterest(calculationContext.getLoaneeLoanDetail(), outstanding, currentDate.atStartOfDay());
            monthlyInterestAccrued = monthlyInterestAccrued.add(dailyInterest);
            interestAccruedBeforeRepayment = interestAccruedBeforeRepayment.add(dailyInterest);
            List<RepaymentHistory> repayments = calculationContext.getRepaymentHistories();

            // check if repayment on this day
            while (repaymentIndex < repayments.size() &&
                    repayments.get(repaymentIndex).getPaymentDateTime().toLocalDate().isEqual(currentDate)) {
                RepaymentHistory repaymentHistory = repayments.get(repaymentIndex);
                validateAmountRepaid(repaymentHistory);
                outstanding = decimalPlaceRoundUp(outstanding.subtract(repaymentHistory.getAmountPaid()));
                totalAmountRepaid = decimalPlaceRoundUp(totalAmountRepaid.add(repaymentHistory.getAmountPaid()));
                repaymentHistory.setAmountOutstanding(outstanding);
                log.info("Interest accrued before repayment {}", interestAccruedBeforeRepayment);
                repaymentHistory.setInterestIncurred(interestAccruedBeforeRepayment);

                log.info("Repayment made on {} amount paid {} amount outstanding {} current total amount repaid is {} interest incurred till today {}",
                        repaymentHistory.getPaymentDateTime(), repaymentHistory.getAmountPaid(), outstanding, totalAmountOutstanding, interestAccruedBeforeRepayment);
                interestAccruedBeforeRepayment = BigDecimal.ZERO;
                updateRepaymentMeta(repaymentHistory, calculationContext);
                repaymentIndex++;
            }

            // if end of month and not the current month
            if (isEndOfMonth(currentDate) && !isSameMonth(currentDate, asOfDate)) {
                log.info("Its end of month but not this month {}", currentDate);
                outstanding = decimalPlaceRoundUp(outstanding.add(monthlyInterestAccrued));
                totalAmountOutstanding = outstanding;
                totalInterestIncurred = decimalPlaceRoundUp(totalInterestIncurred.add(monthlyInterestAccrued));
                saveMonthlyInterest(monthlyInterestAccrued, calculationContext.getLoaneeLoanDetail(), currentDate.atStartOfDay());
                log.info("End of month calculations. New outstanding is {} interest incurred this month is {} date is {}", outstanding, monthlyInterestAccrued, currentDate);
                monthlyInterestAccrued = BigDecimal.ZERO;
            }

            currentDate = currentDate.plusDays(1);
        }
        calculationContext.getLoaneeLoanDetail().setAmountOutstanding(decimalPlaceRoundUp(totalAmountOutstanding));
        calculationContext.getLoaneeLoanDetail().setAmountRepaid(decimalPlaceRoundUp(totalAmountRepaid));
        calculationContext.getLoaneeLoanDetail().setInterestIncurred(decimalPlaceRoundUp(totalInterestIncurred));
        log.info("\n --------------------------------------- >>>>>>>>>>>>>>>>>>> Total interest incurred is {}, total amount outstanding is {} ,total amount repaid is {}", calculationContext.getLoaneeLoanDetail().getInterestIncurred(), calculationContext.getLoaneeLoanDetail().getAmountOutstanding(), calculationContext.getLoaneeLoanDetail().getAmountRepaid());
    }

    private static boolean isEndOfMonth(LocalDate date) {
        return date.getDayOfMonth() == date.lengthOfMonth();
    }

    private static boolean isSameMonth(LocalDate currentDate, LocalDate endDate) {
        return currentDate.getMonth() == endDate.getMonth() && currentDate.getYear() == endDate.getYear();
    }

    private List<RepaymentHistory> combineAndSortRepaymentHistories(List<RepaymentHistory> repaymentHistories, List<RepaymentHistory> previousRepaymentHistory) throws MeedlException {
        repaymentHistories = combinePreviousAndNewRepaymentHistory(previousRepaymentHistory, repaymentHistories);
        repaymentHistories = sortRepaymentsByDateTimeAscending(repaymentHistories);
        return repaymentHistories;
    }

    private void updateLoaneeRepaymentHistory(List<RepaymentHistory> currentRepaymentHistories, List<RepaymentHistory> previousRepaymentHistory) {
        log.info("updating list of loanee repayment histories {}", currentRepaymentHistories);
        List<String> repaymentHistoryIds = previousRepaymentHistory.stream()
                .map(RepaymentHistory::getId)
                .toList();
        repaymentHistoryOutputPort.deleteMultipleRepaymentHistory(repaymentHistoryIds);
        log.info("After deleting multiple repayments... ");
        currentRepaymentHistories.forEach(repaymentHistory -> log.info("{}", repaymentHistory.getInterestIncurred()));
        currentRepaymentHistories = repaymentHistoryOutputPort.saveAllRepaymentHistory(currentRepaymentHistories);
        log.info("After saving multiple repayment history for a loanee {}", currentRepaymentHistories);
    }

    @Override
    public BigDecimal calculateCurrentAmountPaid(List<RepaymentHistory> repaymentHistories) {
        return repaymentHistories.stream()
                .map(RepaymentHistory::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    BigDecimal getPreviousAmountOutstanding(BigDecimal previousOutstanding, LoaneeLoanDetail loaneeLoanDetail) {
        if (ObjectUtils.isNotEmpty(previousOutstanding)) {
            log.info("Getting the previous amount outstanding as the previous in the calculation {}", previousOutstanding);
            return decimalPlaceRoundUp(previousOutstanding);
        }
        log.info("Getting the previous amount outstanding as amount received {}", loaneeLoanDetail.getAmountReceived());
        return decimalPlaceRoundUp(loaneeLoanDetail.getAmountReceived());
    }

    private LoaneeLoanDetail getLoaneeLoanDetail(CalculationContext context) throws MeedlException {
        CohortLoanee cohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(context.getLoanee().getId(), context.getCohort().getId());
        LoaneeLoanDetail loaneeLoanDetail = loaneeLoanDetailsOutputPort.findByCohortLoaneeId(cohortLoanee.getId());
        context.setPreviousTotalAmountPaid(loaneeLoanDetail.getAmountRepaid());
        context.setPreviousTotalInterestIncurred(loaneeLoanDetail.getInterestIncurred());
        return loaneeLoanDetail;
    }


    public BigDecimal calculateInterest(double interestRate, BigDecimal outstanding, long daysBetween) {

        BigDecimal interestRateInPercent = BigDecimal.valueOf(interestRate)
                .divide(PERCENTAGE_BASE, NUMBER_OF_DECIMAL_PLACES + ADDITIONAL_PRECISION_SCALE, RoundingMode.HALF_UP);
        BigDecimal dailyRate = interestRateInPercent.divide(BigDecimal.valueOf(DAYS_IN_YEAR), NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);

        log.info("Calculated daily rate ==== {} for annual interest rate {}, interest rate in percent {}", dailyRate, interestRate, interestRateInPercent);

        return outstanding.multiply(dailyRate).multiply(BigDecimal.valueOf(daysBetween));
    }

    private static void validateAmountRepaid(RepaymentHistory repayment) throws MeedlException {
        if (ObjectUtils.isEmpty(repayment) || ObjectUtils.isEmpty(repayment.getAmountPaid())) {
            log.error("Repayment does not have amount paid ---- {}", repayment);
            throw new MeedlException("Repayment amount must be provided.");
        }
    }

    public List<RepaymentHistory> combinePreviousAndNewRepaymentHistory(
            List<RepaymentHistory> previousRepaymentHistory,
            List<RepaymentHistory> newRepaymentHistories
    ) {
        if (ObjectUtils.isNotEmpty(previousRepaymentHistory) && !previousRepaymentHistory.isEmpty()) {
            List<RepaymentHistory> mergedList = new ArrayList<>(previousRepaymentHistory.size() + newRepaymentHistories.size());
            mergedList.addAll(previousRepaymentHistory);
            mergedList.addAll(newRepaymentHistories);
            return mergedList;
        }
        return newRepaymentHistories;
    }


    private static void validationForCalculatingTotalAmountRepaid(RepaymentHistory repayment) throws MeedlException {
        if (repayment == null) {
            log.warn("Repayment history cannot be null, while calculating total amount repaid");
            throw new MeedlException("Repayment history must be provided.");
        }
        log.info("Amount repaid {} on {}", repayment.getAmountPaid(), repayment.getPaymentDateTime());
        if (repayment.getAmountPaid() == null) {
            log.warn("Payment amount cannot be null");
            throw new MeedlException("Payment amount must be provided.");
        }
        if (repayment.getAmountPaid().compareTo(BigDecimal.ZERO) < MINIMUM_VALID_NUMBER) {
            log.warn("Amount paid on {} can not be negative: {}", repayment.getPaymentDateTime(), repayment.getAmountPaid());
            throw new MeedlException("Amount paid can not be negative : " + repayment.getAmountPaid());
        }
    }


    //    @Override
    public BigDecimal calculateLoanAmountRequested(BigDecimal programFee, BigDecimal initialDeposit) throws MeedlException {
        validateAmount(programFee, "Program Fee");
        validateAmount(initialDeposit, "Initial Deposit");

        return programFee.subtract(initialDeposit);
    }

    //    @Override
    public BigDecimal calculateLoanAmountDisbursed(BigDecimal loanAmountRequested, BigDecimal loanDisbursementFees) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateAmount(loanDisbursementFees, "Loan Disbursement Fees");

        return loanAmountRequested.add(loanDisbursementFees);
    }

    //    @Override
    public BigDecimal calculateLoanAmountOutstanding(
            BigDecimal loanAmountOutstanding,
            BigDecimal monthlyRepayment,
            int moneyWeightedPeriodicInterest
    ) throws MeedlException {
        validateAmount(loanAmountOutstanding, "Loan Amount Outstanding");
        validateAmount(monthlyRepayment, "Monthly Repayment");
        validateInterestRate(moneyWeightedPeriodicInterest, "Money Weighted Periodic Interest");

        return loanAmountOutstanding
                .subtract(monthlyRepayment)
                .add(BigDecimal.valueOf(moneyWeightedPeriodicInterest));
    }

    //    @Override
    public BigDecimal calculateMoneyWeightedPeriodicInterest(int interestRate, List<LoanPeriodRecord> periods) throws MeedlException {
        validateInterestRate(interestRate, "Interest rate");

        BigDecimal sumProduct = BigDecimal.ZERO;
        for (LoanPeriodRecord record : periods) {
            validateLoanPeriodRecord(record);
            BigDecimal product = record.getLoanAmountOutstanding().multiply(BigDecimal.valueOf(record.getDaysHeld()));
            sumProduct = sumProduct.add(product);
        }

        BigDecimal rateFraction = BigDecimal.valueOf(interestRate)
                .divide(BigDecimal.valueOf(DAYS_IN_YEAR), NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);
        return rateFraction.multiply(sumProduct);
    }

    //    @Override
    public int calculateMonthlyInterestRate(int interestRate) throws MeedlException {
        validateInterestRate(interestRate, "Interest rate");
        return interestRate / MONTHS_PER_YEAR;
    }

    //    @Override
    public BigDecimal calculateManagementOrProcessingFee(BigDecimal loanAmountRequested, double mgtFeeInPercentage) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateInterestRate(mgtFeeInPercentage, "Management Fee Percentage");

        BigDecimal percentageDecimal = BigDecimal.valueOf(mgtFeeInPercentage).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        return decimalPlaceRoundUp(loanAmountRequested.multiply(percentageDecimal));
    }

    //    @Override
    public BigDecimal calculateCreditLife(BigDecimal loanAmountRequested, int creditLifePercentage, int loanTenureMonths) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateInterestRate(creditLifePercentage, "Credit Life Percentage");
        validateLoanTenure(loanTenureMonths);

        BigDecimal percentage = BigDecimal.valueOf(creditLifePercentage)
                .divide(PERCENTAGE_BASE, NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);

        double tenureYears = loanTenureMonths / MONTHS_PER_YEAR_DOUBLE;
        int multiplier = Math.max(MINIMUM_MULTIPLIER, (int) Math.ceil(tenureYears));

        return decimalPlaceRoundUp(
                loanAmountRequested
                        .multiply(percentage)
                        .multiply(BigDecimal.valueOf(multiplier))
        );
    }

    //    @Override
    public BigDecimal calculateLoanDisbursementFee(Map<String, BigDecimal> feeMap) throws MeedlException {
        if (feeMap == null || feeMap.isEmpty()) {
            return decimalPlaceRoundUp(BigDecimal.ZERO);
        }

        double[] values = new double[feeMap.size()];
        int i = 0;

        for (Map.Entry<String, BigDecimal> entry : feeMap.entrySet()) {
            String name = entry.getKey();
            BigDecimal value = entry.getValue();
            validateAmount(value, name);
            values[i++] = value.doubleValue();
        }

        double sum = StatUtils.sum(values);
        return decimalPlaceRoundUp(BigDecimal.valueOf(sum));
    }

    //    @Override
    public BigDecimal calculateLoanDisbursementFees(Map<String, BigDecimal> feeMap) throws MeedlException {
        if (feeMap == null || feeMap.isEmpty()) {
            return decimalPlaceRoundUp(BigDecimal.ZERO);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : feeMap.entrySet()) {
            String name = entry.getKey();
            BigDecimal value = entry.getValue();

            validateAmount(value, name);
            total = total.add(value);
        }

        return decimalPlaceRoundUp(total);
    }

    //    @Override
    public BigDecimal calculateTotalRepayment(List<BigDecimal> monthlyRepayments) throws MeedlException {
        if (monthlyRepayments == null || monthlyRepayments.isEmpty()) {
            log.warn("The monthly repayment list was empty. Returned zero. ");
            return decimalPlaceRoundUp(BigDecimal.ZERO);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < monthlyRepayments.size(); i++) {
            BigDecimal repayment = monthlyRepayments.get(i);
            validateAmount(repayment, "Monthly Repayment " + i);
            total = total.add(repayment);
        }
        return decimalPlaceRoundUp(total);
    }

    private BigDecimal decimalPlaceRoundUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    private void validateLoanTenure(int tenure) throws MeedlException {
        if (tenure < MINIMUM_VALID_NUMBER) {
            log.error("Loan Tenure must not be negative. Validation in loan calculation service.");
            throw new MeedlException("Loan Tenure must not be negative.");
        }
    }

    private void validateLoanPeriodRecord(LoanPeriodRecord record) throws MeedlException {
        if (record == null) {
            log.error("Loan period record must not be null - {}. ", record);
            throw new MeedlException("Loan period record must not be null.");
        }
        if (record.getLoanAmountOutstanding() == null) {
            log.error("Loan Amount Outstanding must not be null.");
            throw new MeedlException("Loan Amount Outstanding must not be null.");
        }
        if (record.getLoanAmountOutstanding().compareTo(BigDecimal.ZERO) < MINIMUM_VALID_NUMBER) {
            log.error("In validateLoanPeriodRecord Method. Loan Amount Outstanding must not be negative.");
            throw new MeedlException("Loan Amount Outstanding must not be negative.");
        }
        if (record.getDaysHeld() < MINIMUM_VALID_NUMBER) {
            log.error("Days Held must not be negative.");
            throw new MeedlException("Days Held must not be negative.");
        }
    }

    private void validateInterestRate(int interestRate, String name) throws MeedlException {
        if (interestRate < MINIMUM_VALID_NUMBER) {
            log.error("{} must not be negative.  In validate interest rate.", name);
            throw new MeedlException(name + " must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate.", name);
            throw new MeedlException(name + " must not exceed 100.");
        }
    }

    private void validateInterestRate(double interestRate, String name) throws MeedlException {
        if (interestRate < MINIMUM_VALID_NUMBER) {
            log.error("{} must not be negative. In validate interest rate as double", name);
            throw new MeedlException(name + " must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate. as double", name);
            throw new MeedlException(name + " must not exceed 100.");
        }
    }

    public void validateAmount(BigDecimal amount, String name) throws MeedlException {
        if (amount == null) {
            log.error("{} must not be null. In validate amount", name);
            throw new MeedlException(name + " must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < MINIMUM_VALID_NUMBER) {
            log.error("{} must not be negative. In validate amount", name);
            throw new MeedlException(name + " must not be negative.");
        }
    }

    @Override
    public void scheduleDailyInterestCalculation() {
        jobScheduler.scheduleRecurrently(
                "daily-interest-calculation-11-30-PM",
                "0 30 23 * * *",  // every day at 11:30  PM
                this::calculateDailyInterest
        );
    }

    @Override
    public void scheduleMonthlyInterestCalculation(){
        jobScheduler.scheduleRecurrently(
                "monthly-interest-calculation-every-last-day-of-the-month-11-40-PM",
                "0 40 23 L * *", // every ladt day of the month at 11:40 PM
                this::calculateMonthlyInterest
        );
    }

    public void calculateMonthlyInterest() throws MeedlException {
        List<LoaneeLoanDetail> loaneeLoanDetails =
                loaneeLoanDetailsOutputPort.findAllWithDailyInterestByMonthAndYear(LocalDate.now().getMonth(), LocalDate.now().getYear());
        for (LoaneeLoanDetail loaneeLoanDetail : loaneeLoanDetails) {
            BigDecimal accumulatedInterestForTheMonth = sumAccumulatedInterestForTheMonth(loaneeLoanDetail);
            MonthlyInterest foundMonthlyInterest = monthlyInterestOutputPort.findByDateCreated(LocalDateTime.now(),loaneeLoanDetail.getId());
            if (ObjectUtils.isEmpty(foundMonthlyInterest)) {

                MonthlyInterest monthlyInterest = saveMonthlyInterest(accumulatedInterestForTheMonth, loaneeLoanDetail, LocalDateTime.now());

                updateInterestIncurredOnLoaneeLoanDetail(loaneeLoanDetail, accumulatedInterestForTheMonth, monthlyInterest);

                CohortLoanDetail cohortLoanDetail = updateInterestIncurredOnCohortLoanDetail(loaneeLoanDetail, monthlyInterest);

                updateLoaneeLoanAggregation(loaneeLoanDetail, monthlyInterest);

                ProgramLoanDetail programLoanDetail = updateInterestIncurredOnProgramLoanDetail(cohortLoanDetail, monthlyInterest);
                updateInterestIncurredOnOrganizationLoanDetail(programLoanDetail, monthlyInterest);
            }
        }

    }

    private void updateLoaneeLoanAggregation(LoaneeLoanDetail loaneeLoanDetail, MonthlyInterest monthlyInterest) throws MeedlException {
        LoaneeLoanAggregate loaneeLoanAggregate =
                loaneeLoanAggregateOutputPort.findByLoaneeLoanAggregateByLoaneeLoanDetailId(loaneeLoanDetail.getId());
        loaneeLoanAggregate.setTotalAmountOutstanding(loaneeLoanAggregate.getTotalAmountOutstanding().add(monthlyInterest.getInterest()));
        loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
    }

    private BigDecimal sumAccumulatedInterestForTheMonth(LoaneeLoanDetail loaneeLoanDetail) throws MeedlException {
        BigDecimal accumulatedInterestForTheMonth = dailyInterestOutputPort
                .findAllInterestForAMonth(LocalDate.now().getMonth(), LocalDate.now().getYear(), loaneeLoanDetail.getId())
                .stream()
                .map(DailyInterest::getInterest)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Done calculating accumulated interest for the month == {} year == {} for {}. Accumulated interest is {}",
                LocalDate.now().getMonth(), LocalDate.now().getYear(), loaneeLoanDetail.getId(), accumulatedInterestForTheMonth);
        return accumulatedInterestForTheMonth;
    }

    private void updateInterestIncurredOnOrganizationLoanDetail(ProgramLoanDetail programLoanDetail, MonthlyInterest monthlyInterest) throws MeedlException {
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(programLoanDetail.getProgram().getOrganizationIdentity().getId());
        log.info("Organization interest incurred as at last month == {} before adding this month incurred == {}",
                organizationLoanDetail.getInterestIncurred(), monthlyInterest.getInterest());
        log.info("Organization amount outstanding as at last month == {} before adding this month incurred == {}",
                organizationLoanDetail.getOutstandingAmount(), monthlyInterest.getInterest());

        organizationLoanDetail.setInterestIncurred(organizationLoanDetail.getInterestIncurred().add(monthlyInterest.getInterest()));
        organizationLoanDetail.setOutstandingAmount(organizationLoanDetail.getOutstandingAmount().add(monthlyInterest.getInterest()));

        log.info("Organization interest incurred after adding this month incurred == {}", organizationLoanDetail.getInterestIncurred());
        log.info("Organization amount outstanding after adding this month incurred == {}", organizationLoanDetail.getOutstandingAmount());
        organizationLoanDetailOutputPort.save(organizationLoanDetail);
    }

    private ProgramLoanDetail updateInterestIncurredOnProgramLoanDetail(CohortLoanDetail cohortLoanDetail, MonthlyInterest monthlyInterest) throws MeedlException {
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(cohortLoanDetail.getCohort().getProgramId());
        log.info("Program interest incurred as at last month == {} before adding this month incurred == {}",
                programLoanDetail.getInterestIncurred(), monthlyInterest.getInterest());
        log.info("Program amount outstanding as at last month == {} before adding this month incurred == {}",
                programLoanDetail.getOutstandingAmount(), monthlyInterest.getInterest());

        programLoanDetail.setInterestIncurred(programLoanDetail.getInterestIncurred().add(monthlyInterest.getInterest()));
        programLoanDetail.setOutstandingAmount(programLoanDetail.getOutstandingAmount().add(monthlyInterest.getInterest()));

        log.info("Program interest incurred after adding this month incurred == {}", programLoanDetail.getInterestIncurred());
        log.info("Program amount outstanding after adding this month incurred == {}", programLoanDetail.getOutstandingAmount());
        programLoanDetailOutputPort.save(programLoanDetail);
        return programLoanDetail;
    }

    private CohortLoanDetail updateInterestIncurredOnCohortLoanDetail(LoaneeLoanDetail loaneeLoanDetail, MonthlyInterest monthlyInterest) throws MeedlException {
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByLoaneeLoanDetailId(loaneeLoanDetail.getId());
        log.info("Cohort interest incurred as at last month == {} before adding this month incurred == {}",
                cohortLoanDetail.getInterestIncurred(), monthlyInterest.getInterest());
        log.info("Cohort amount outstanding as at last month == {} before adding this month incurred == {}",
                cohortLoanDetail.getOutstandingAmount(), monthlyInterest.getInterest());

        cohortLoanDetail.setInterestIncurred(cohortLoanDetail.getInterestIncurred().add(monthlyInterest.getInterest()));
        cohortLoanDetail.setOutstandingAmount(cohortLoanDetail.getOutstandingAmount().add(monthlyInterest.getInterest()));

        log.info("Cohort interest incurred after adding this month incurred == {}", cohortLoanDetail.getInterestIncurred());
        log.info("Cohort amount outstanding after adding this month incurred == {}", cohortLoanDetail.getOutstandingAmount());
        cohortLoanDetailOutputPort.save(cohortLoanDetail);
        return cohortLoanDetail;
    }

    private void updateInterestIncurredOnLoaneeLoanDetail(LoaneeLoanDetail loaneeLoanDetail, BigDecimal accumulatedInterestForTheMonth, MonthlyInterest monthlyInterest) {
        log.info("Interest incurred as at last month == {} before adding this month incurred == {}",
                loaneeLoanDetail.getInterestIncurred(), accumulatedInterestForTheMonth);
        log.info("Amount outstanding as at last month == {} before adding this month incurred == {}",
                loaneeLoanDetail.getAmountOutstanding(), accumulatedInterestForTheMonth);

        loaneeLoanDetail.setInterestIncurred(loaneeLoanDetail.getInterestIncurred().add(monthlyInterest.getInterest()));
        loaneeLoanDetail.setAmountOutstanding(loaneeLoanDetail.getAmountOutstanding().add(monthlyInterest.getInterest()));

        log.info("Interest incurred after adding this month incurred == {}", loaneeLoanDetail.getInterestIncurred());
        log.info("Amount outstanding after adding this month incurred == {}", loaneeLoanDetail.getAmountOutstanding());
        loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
    }

    public void calculateInterestForEachMonthWithNoRepayment(LocalDate month, CalculationContext calculationContext) throws MeedlException {
        int numberOfDaysTillDateMeasured = month.getDayOfMonth();
        int startDay = calculationContext.getStartDate().getDayOfMonth();
        int lastDayOfMonth = month.lengthOfMonth();

        LoaneeLoanDetail loaneeLoanDetail = calculationContext.getLoaneeLoanDetail();
        log.info("calculate Interest For Each Month With No Repayment Loanee loan details id {} ", loaneeLoanDetail.getId());
        for (int day = startDay; day <= numberOfDaysTillDateMeasured; day++) {
            DailyInterest dailyInterest = calculateAndSaveDailyInterest(loaneeLoanDetail, calculationContext.getPreviousOutstandingAmount(),  month.withDayOfMonth(day).atStartOfDay());
            calculationContext.setTotalInterestIncurredInAMonth(calculationContext.getTotalInterestIncurredInAMonth().add(dailyInterest.getInterest()));
        }
        if (isLastDayOfTheMonth(numberOfDaysTillDateMeasured, lastDayOfMonth)) {
            log.info("No monthly interest found for {} saving new monthly interest", month.atStartOfDay());
            calculateAndSaveMonthlyInterest(calculationContext, loaneeLoanDetail, month.atStartOfDay());
            setStartDateToNextFirstOfNextMonth(calculationContext);
        }
    }

    private void calculateAndSaveMonthlyInterest(CalculationContext calculationContext, LoaneeLoanDetail loaneeLoanDetail, LocalDateTime dateCreated) throws MeedlException {
        saveMonthlyInterest(calculationContext.getTotalInterestIncurredInAMonth(), loaneeLoanDetail, dateCreated);
        calculationContext.setPreviousOutstandingAmount(
                calculationContext
                        .getPreviousOutstandingAmount()
                        .add(calculationContext
                                .getTotalInterestIncurredInAMonth()));
        calculationContext
                .setTotalInterestIncurred(
                        calculationContext
                                .getTotalInterestIncurred()
                                .add(calculationContext.getTotalInterestIncurredInAMonth()));
        calculationContext.setTotalInterestIncurredInAMonth(BigDecimal.ZERO);
    }

    private void setStartDateToNextFirstOfNextMonth(CalculationContext calculationContext) {
        LocalDateTime nextMonthStart = calculationContext.getStartDate()
                .plusMonths(1)       // move one month ahead
                .withDayOfMonth(1)   // set to first day
                .withHour(0).withMinute(0).withSecond(0).withNano(0); // normalize to midnight if you want

        calculationContext.setStartDate(nextMonthStart);
        log.info("Updated start date to next month: {}", nextMonthStart);
    }

    private MonthlyInterest saveMonthlyInterest(BigDecimal interestIncurredInMonth, LoaneeLoanDetail loaneeLoanDetail, LocalDateTime dateCreated) throws MeedlException {
        MonthlyInterest monthlyInterest = monthlyInterestOutputPort.findByDateCreated(dateCreated,loaneeLoanDetail.getId());
        if (Objects.isNull(monthlyInterest)) {
            MonthlyInterest buildMonthlyInterest = MonthlyInterest.builder()
                    .interest(decimalPlaceRoundUp(interestIncurredInMonth))
                    .createdAt(dateCreated)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .build();

            log.info("Monthly interest before saving == {}", buildMonthlyInterest);
            buildMonthlyInterest = monthlyInterestOutputPort.save(buildMonthlyInterest);
            log.info("Monthly interest after saving == {}", buildMonthlyInterest);
            return buildMonthlyInterest;
        }
        return monthlyInterest;
    }

    private boolean isLastDayOfTheMonth(int countInDays, int lastDayOfMonth) {
        return countInDays == lastDayOfMonth;
    }

    public void calculateDailyInterest() throws MeedlException {
        List<LoaneeLoanDetail> loaneeLoanDetails = loaneeLoanDetailsOutputPort.findAllByNotNullAmountOutStanding();

        for (LoaneeLoanDetail loaneeLoanDetail : loaneeLoanDetails) {
            calculateAndSaveDailyInterest(loaneeLoanDetail, loaneeLoanDetail.getAmountOutstanding(), LocalDateTime.now());
        }
    }

    private DailyInterest calculateAndSaveDailyInterest(LoaneeLoanDetail loaneeLoanDetail, BigDecimal amountOutstanding, LocalDateTime dateCreated) throws MeedlException {
        DailyInterest dailyInterest = dailyInterestOutputPort.findDailyInterestForDate(dateCreated,loaneeLoanDetail.getId());
        if (ObjectUtils.isEmpty(dailyInterest)) {
            BigDecimal dailyInterestIncurred =
                    calculateInterest(loaneeLoanDetail.getInterestRate(), amountOutstanding, 1);
            log.info("daily interest  {}", dailyInterestIncurred);
            DailyInterest buildDailyInterest = DailyInterest.builder()
                    .interest(decimalPlaceRoundUp(dailyInterestIncurred))
                    .createdAt(dateCreated)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .build();
            log.info("daily interest before saving === : {}", buildDailyInterest);
            buildDailyInterest = dailyInterestOutputPort.save(buildDailyInterest);
            log.info("saved daily interest === : {}", buildDailyInterest);
            return buildDailyInterest;
        }
        return dailyInterest;
    }

}

