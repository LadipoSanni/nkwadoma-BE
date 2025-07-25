package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.CalculationContext;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalculationEngine implements CalculationEngineUseCase {
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final int NUMBER_OF_DECIMAL_PLACE = 8;
    private final int DAYS_IN_MONTH = 30;
    private final int DAYS_IN_YEAR = 365;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final ProgramLoanDetailOutputPort programLoanDetailOutputPort;

    @Override
    public List<RepaymentHistory> sortRepaymentsByDateTimeAscending(List<RepaymentHistory> repayments) throws MeedlException {
        log.info("Started the sorting ");
        if (repayments == null) {
            log.warn("Repayments was null in the sorting method");
            return Collections.emptyList();
        }
        List<RepaymentHistory> mutableRepayments = new ArrayList<>(repayments);
        log.info("The repayment list before sorting {} \n --------------------------------------------    Repayments are not empty and the sorting has started. The number of the repayment is :{}",mutableRepayments, repayments.size());
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

    @Override
    public void calculateLoaneeLoanRepaymentHistory(
            List<RepaymentHistory> repaymentHistories,
            Loanee loanee,
            Cohort cohort
    ) throws MeedlException {
        if (isSkipableCalculation(repaymentHistories, loanee)) return;
        CalculationContext context = CalculationContext.builder().cohort(cohort).loanee(loanee).build();

        LoaneeLoanDetail loaneeLoanDetail = getLoaneeLoanDetail(context);
        List<RepaymentHistory> previousRepaymentHistory = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loanee.getId(), cohort.getId());
        List<RepaymentHistory> allRepayments = combineAndSortRepaymentHistories(repaymentHistories, previousRepaymentHistory);

        processRepaymentHistoryCalculations(allRepayments, loanee, cohort, loaneeLoanDetail);

        loaneeLoanDetail = finalizeRepaymentHistoryCalculation(allRepayments, previousRepaymentHistory, loaneeLoanDetail);
        context.setLoaneeLoanDetail(loaneeLoanDetail);
        updateLoanDetails(context);
    }
    private void updateLoanDetails(CalculationContext context) throws MeedlException {
        calculateCohortLoanDetail(context);
        ProgramLoanDetail programLoanDetail = updateProgramLoanDetail(context);

        OrganizationLoanDetail organizationLoanDetail = updateOrganizationLoanDetail(programLoanDetail, currentAmountPaid);
        log.info("Organization loan details after saving {}",organizationLoanDetail);

    }
    private void calculateCohortLoanDetail(CalculationContext context) throws MeedlException {
        log.info("About to Update Cohort loan detail after repayment ");
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(context.getCohort().getId());
        log.info("Cohort loan detail found {} \n  ---------------------> previous cohort total amount outstanding {}", cohortLoanDetail, cohortLoanDetail.getTotalOutstandingAmount());

        context.setCohortLoanDetail(cohortLoanDetail);
        BigDecimal newCohortTotalOutstandingAmount = calculateCohortTotalOutstandingAmount(context);
        cohortLoanDetail.setTotalOutstandingAmount(newCohortTotalOutstandingAmount);
        log.info("new cohort total outstanding amount {}, \n -------------------------------> Previous cohort total amount repaid {} ", newCohortTotalOutstandingAmount,  cohortLoanDetail.getTotalAmountRepaid());

        BigDecimal newCohortTotalAmountRepaid = calculateCohortTotalAmountRepaid(context);
        cohortLoanDetail.setTotalAmountRepaid(newCohortTotalAmountRepaid);
        log.info("New cohort total amount repaid {} \n --------------------------------> previous cohort interest incurred {} ", newCohortTotalAmountRepaid, cohortLoanDetail.getTotalInterestIncurred());

        BigDecimal newCohortTotalInterestIncurred = calculateCohortTotalInterestIncurred(context);
        cohortLoanDetail.setTotalInterestIncurred(newCohortTotalInterestIncurred);
        log.info("New Cohort total interest incurred  {}", newCohortTotalInterestIncurred);

        cohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
        log.info("cohort loan details after saving {}",cohortLoanDetail);
    }

    private BigDecimal calculateCohortTotalInterestIncurred(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getTotalInterestIncurred()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getLoaneeLoanDetail().getInterestIncurred());
    }

    private BigDecimal calculateCohortTotalAmountRepaid(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getTotalAmountRepaid()
                .subtract(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private BigDecimal calculateCohortTotalOutstandingAmount(CalculationContext context) {
        return context.getCohortLoanDetail()
                    .getTotalOutstandingAmount()
                    .subtract(context.getPreviousTotalInterestIncurred())
                    .add(context.getPreviousTotalAmountPaid())
                    .add(context.getLoaneeLoanDetail().getInterestIncurred())
                    .subtract(context.getLoaneeLoanDetail().getAmountRepaid());
    }


    private BigDecimal calculateProgramTotalInterestIncurred(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getTotalInterestIncurred()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getLoaneeLoanDetail().getInterestIncurred());
    }

    private BigDecimal calculateProgramTotalAmountRepaid(CalculationContext context) {
        return context.getCohortLoanDetail()
                .getTotalAmountRepaid()
                .subtract(context.getPreviousTotalAmountPaid())
                .add(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private BigDecimal calculateProgramTotalOutstandingAmount(CalculationContext context) {
        return context.getProgramLoanDetail()
                .getTotalOutstandingAmount()
                .subtract(context.getPreviousTotalInterestIncurred())
                .add(context.getPreviousTotalAmountPaid())
                .add(context.getCohortLoanDetail().getTotalInterestIncurred())
                .subtract(context.getLoaneeLoanDetail().getAmountRepaid());
    }

    private ProgramLoanDetail updateProgramLoanDetail(CalculationContext context) throws MeedlException {

        log.info("About to Update Program loan detail after repayment ");
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(context.getCohortLoanDetail().getCohort().getProgramId());
        log.info("Program loan detail found {} \n  ---------------------> previous program total amount outstanding {}", programLoanDetail, programLoanDetail.getTotalOutstandingAmount());

        context.setProgramLoanDetail(programLoanDetail);
        BigDecimal newProgramTotalOutstandingAmount = calculateProgramTotalOutstandingAmount(context);
        programLoanDetail.setTotalOutstandingAmount(newProgramTotalOutstandingAmount);
        log.info("new program total outstanding amount {}, \n -------------------------------> Previous program total amount repaid {} ", newProgramTotalOutstandingAmount,  programLoanDetail.getTotalAmountRepaid());

        BigDecimal newProgramTotalAmountRepaid = calculateProgramTotalAmountRepaid(context);
        programLoanDetail.setTotalAmountRepaid(newProgramTotalAmountRepaid);
        log.info("New program total amount repaid {} \n --------------------------------> previous program interest incurred {} ", newProgramTotalAmountRepaid, programLoanDetail.getTotalInterestIncurred());

        BigDecimal newProgramTotalInterestIncurred = calculateProgramTotalInterestIncurred(context);
        programLoanDetail.setTotalInterestIncurred(newProgramTotalInterestIncurred);
        log.info("New program total interest incurred  {}", newProgramTotalInterestIncurred);

        programLoanDetail = programLoanDetailOutputPort.save(programLoanDetail);
        log.info("program loan details after saving {}",programLoanDetail);

    }
    private OrganizationLoanDetail updateOrganizationLoanDetail(ProgramLoanDetail programLoanDetail, BigDecimal currentAmountPaid) throws MeedlException {
        log.info("About to Update Organization loan detail after repayment ");
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(
                programLoanDetail.getProgram().getOrganizationIdentity().getId());
        log.info("organization loan detail found {}", organizationLoanDetail);
        organizationLoanDetail.setTotalAmountRepaid(organizationLoanDetail.getTotalAmountRepaid().add(currentAmountPaid));
        organizationLoanDetail.setTotalOutstandingAmount(organizationLoanDetail.getTotalOutstandingAmount().subtract(currentAmountPaid));
        log.info("Updated Organization loan detail after repayment  {}", organizationLoanDetail);
        organizationLoanDetail = organizationLoanDetailOutputPort.save(organizationLoanDetail);
        return organizationLoanDetail;
    }
    private LoaneeLoanDetail finalizeRepaymentHistoryCalculation(
            List<RepaymentHistory> currentRepayments,
            List<RepaymentHistory> previousRepayments,
            LoaneeLoanDetail loaneeLoanDetail
    ) {
        loaneeLoanDetail.setUpdatedAt(LocalDateTime.now());
        LoaneeLoanDetail updatedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        log.info("Loanee loan details updated with repayment calculations. {}", updatedLoaneeLoanDetail);
        updateLoaneeRepaymentHistory(currentRepayments, previousRepayments);
        return updatedLoaneeLoanDetail;
    }

    private void updateLoaneeLoanDetailWithRunningTotals(
            LoaneeLoanDetail loaneeLoanDetail,
            BigDecimal outstanding,
            BigDecimal repaid
    ) {
        loaneeLoanDetail.setAmountOutstanding(decimalPlaceRoundUp(outstanding));
        loaneeLoanDetail.setAmountRepaid(decimalPlaceRoundUp(repaid));
    }

    private void updateRepaymentMeta(RepaymentHistory repayment, Loanee loanee, Cohort cohort) {
        repayment.setCohort(cohort);
        repayment.setLoanee(loanee);
    }

    private boolean isSkipableCalculation(List<RepaymentHistory> repaymentHistories, Loanee loanee) {
        return ObjectUtils.isEmpty(repaymentHistories) || repaymentHistories.isEmpty() || ObjectUtils.isEmpty(loanee);
    }
    private void processRepaymentHistoryCalculations(
            List<RepaymentHistory> repaymentHistories,
            Loanee loanee,
            Cohort cohort,
            LoaneeLoanDetail loaneeLoanDetail
    ) throws MeedlException {

        BigDecimal runningTotal = BigDecimal.ZERO;
        BigDecimal totalInterestIncurred = BigDecimal.ZERO;
        LocalDateTime lastDate = loaneeLoanDetail.getLoanStartDate();
        BigDecimal previousOutstandingAmount = null;

        log.info("Total interest incurred before repayment history calculations begin {}", totalInterestIncurred);
        for (RepaymentHistory repayment : repaymentHistories) {
            validateAmountRepaid(repayment);
            previousOutstandingAmount = getPreviousAmountOutstanding(previousOutstandingAmount, loaneeLoanDetail);
            log.info("Outstanding before processing this repayment {} ", previousOutstandingAmount);

            runningTotal = calculateTotalAmountRepaidPerRepayment(repayment, runningTotal);

            BigDecimal interestIncurred = calculateIncurredInterestPerRepayment(repayment, previousOutstandingAmount, lastDate, loaneeLoanDetail);
            totalInterestIncurred = totalInterestIncurred.add(interestIncurred);

            calculateOutstandingPerRepayment(previousOutstandingAmount, repayment);

            updateRepaymentMeta(repayment, loanee, cohort);
            lastDate = repayment.getPaymentDateTime();
            previousOutstandingAmount = repayment.getAmountOutstanding();

            updateLoaneeLoanDetailWithRunningTotals(loaneeLoanDetail, previousOutstandingAmount, runningTotal);
            log.info("Outstanding per payment {}", previousOutstandingAmount);
        }

        calculateTotalInterestIncurred(loaneeLoanDetail, totalInterestIncurred, lastDate);
    }

    private BigDecimal calculateInterestIncurredFromLastPaymentTillDate(LoaneeLoanDetail loaneeLoanDetail, LocalDateTime lastDate) {
        long daysBetween = calculateDaysBetween(lastDate, LocalDateTime.now());
        BigDecimal incurredInterest = calculateInterest(loaneeLoanDetail.getInterestRate(), loaneeLoanDetail.getAmountOutstanding(), daysBetween);
        log.info("Interest incurred from last payment till today {}, \n ------------------------------> outstanding before adding the interest incurred from last payment {}, ------------------>    total days between from last payment till now is {}", incurredInterest, loaneeLoanDetail.getAmountOutstanding(), daysBetween);
        return incurredInterest;
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
        currentRepaymentHistories = repaymentHistoryOutputPort.saveAllRepaymentHistory(currentRepaymentHistories);
        log.info("After saving multiple repayment history for a loanee {}", currentRepaymentHistories);
    }

    @Override
    public BigDecimal calculateCurrentAmountPaid(List<RepaymentHistory> repaymentHistories) {
        return repaymentHistories.stream()
                .map(RepaymentHistory::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calculateTotalInterestIncurred(LoaneeLoanDetail loaneeLoanDetail, BigDecimal totalInterestIncurred, LocalDateTime lastDate) {

        BigDecimal interestIncurredFromLastPaymentTillDate = calculateInterestIncurredFromLastPaymentTillDate(loaneeLoanDetail, lastDate);
        loaneeLoanDetail.setAmountOutstanding(loaneeLoanDetail.getAmountOutstanding().add(interestIncurredFromLastPaymentTillDate));
        log.info("Outstanding loan amount after adding interest incurred from last payment till date is {}", loaneeLoanDetail.getAmountOutstanding());

        totalInterestIncurred = totalInterestIncurred.add(interestIncurredFromLastPaymentTillDate);
        loaneeLoanDetail.setInterestIncurred(decimalPlaceRoundUp(totalInterestIncurred));
        log.info("Total interest incurred in loanee loan details after all calculations are done ===> {}", loaneeLoanDetail.getInterestIncurred());
    }

    BigDecimal getPreviousAmountOutstanding(BigDecimal previousOutstanding, LoaneeLoanDetail loaneeLoanDetail) {
        if (ObjectUtils.isNotEmpty(previousOutstanding)){
            log.info("Getting the previous amount outstanding as the previous in the calculation {}", previousOutstanding);
            return decimalPlaceRoundUp(previousOutstanding);
        }
        log.info("Getting the previous amount outstanding as amount received {}", loaneeLoanDetail.getAmountReceived());
        return decimalPlaceRoundUp(loaneeLoanDetail.getAmountReceived());
    }

    private  LoaneeLoanDetail getLoaneeLoanDetail(CalculationContext context) throws MeedlException {
        CohortLoanee cohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(context.getLoanee().getId(), context.getCohort().getId());
        LoaneeLoanDetail loaneeLoanDetail = loaneeLoanDetailsOutputPort.findByCohortLoaneeId(cohortLoanee.getId());
        context.setPreviousTotalAmountPaid(loaneeLoanDetail.getAmountRepaid());
        context.setPreviousTotalInterestIncurred(loaneeLoanDetail.getInterestIncurred());
        return loaneeLoanDetail;
    }

    private void calculateOutstandingPerRepayment(BigDecimal previousOutstandingAmount,RepaymentHistory repaymentHistory) {
        log.info("Calculating outstanding per repayment. previous outstanding amount is : {}", previousOutstandingAmount);
        BigDecimal totalDue = previousOutstandingAmount.add(repaymentHistory.getInterestIncurred());
        BigDecimal newOutstandingAmount =  totalDue.subtract(repaymentHistory.getAmountPaid()).max(BigDecimal.ZERO);
        repaymentHistory.setAmountOutstanding(newOutstandingAmount);
    }

    public BigDecimal calculateIncurredInterestPerRepayment(RepaymentHistory repayment, BigDecimal previousOutstandingAmount, LocalDateTime lastDate, LoaneeLoanDetail loaneeLoanDetail) {
        long daysBetween = calculateDaysBetween(lastDate, repayment.getPaymentDateTime());
        log.info("How many days a between the last payment {} \n -------------- >>>>>>>> interest rate {}", daysBetween, loaneeLoanDetail.getInterestRate());
        BigDecimal incurredInterest = calculateInterest(loaneeLoanDetail.getInterestRate(), previousOutstandingAmount, daysBetween);
        log.info("Previous out standing amount after calculating interest incurred {} outstanding is {}", incurredInterest, previousOutstandingAmount);
        repayment.setInterestIncurred(incurredInterest);
        return incurredInterest;
    }

    private long calculateDaysBetween(LocalDateTime lastDate, LocalDateTime currentDate) {
        log.info("Last date {} current date {}", lastDate, currentDate);
        return ChronoUnit.DAYS.between(lastDate, currentDate);
    }

    public BigDecimal calculateInterest(double interestRate, BigDecimal outstanding, long daysBetween) {

        BigDecimal interestRateInPercent = BigDecimal.valueOf(interestRate)
                .divide(BigDecimal.valueOf(100), NUMBER_OF_DECIMAL_PLACE + 4, RoundingMode.HALF_UP) ;
        BigDecimal dailyRate = interestRateInPercent.divide(BigDecimal.valueOf(DAYS_IN_YEAR), NUMBER_OF_DECIMAL_PLACE, RoundingMode.HALF_UP);

        log.info("Calculated daily rate ==== {} for annual interest rate {}, interest rate in percent {}", dailyRate, interestRate, interestRateInPercent);

        return outstanding.multiply(dailyRate).multiply(BigDecimal.valueOf(daysBetween));
    }
    private BigDecimal calculateTotalAmountRepaidPerRepayment(RepaymentHistory repayment, BigDecimal previousTotalAmountRepaid) {
        BigDecimal newTotalAmountRepaid = previousTotalAmountRepaid.add(repayment.getAmountPaid());
        repayment.setTotalAmountRepaid(newTotalAmountRepaid);
        return newTotalAmountRepaid;
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
        if (repayment.getAmountPaid().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Amount paid on {} can not be negative: {}", repayment.getPaymentDateTime(), repayment.getAmountPaid());
            throw new MeedlException("Amount paid can not be negative : "+ repayment.getAmountPaid());
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
                .divide(BigDecimal.valueOf(365), NUMBER_OF_DECIMAL_PLACE, RoundingMode.HALF_UP);
        return rateFraction.multiply(sumProduct);
    }

//    @Override
    public int calculateMonthlyInterestRate(int interestRate) throws MeedlException {
        validateInterestRate(interestRate, "Interest rate");
        return interestRate / 12;
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
                .divide(BigDecimal.valueOf(100), NUMBER_OF_DECIMAL_PLACE, RoundingMode.HALF_UP);

        double tenureYears = loanTenureMonths / 12.0;
        int multiplier = Math.max(1, (int) Math.ceil(tenureYears));

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
            validateAmount(repayment, "Monthly Repayment "+i );
            total = total.add(repayment);
        }
        return decimalPlaceRoundUp(total);
    }
    private BigDecimal decimalPlaceRoundUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(NUMBER_OF_DECIMAL_PLACE, RoundingMode.HALF_UP);
    }
    private void validateLoanTenure(int tenure) throws MeedlException {
        if (tenure < 0) {
            log.error("Loan Tenure must not be negative. Validation in loan calculation service.");
            throw new MeedlException("Loan Tenure must not be negative.");
        }
    }
    private void validateLoanPeriodRecord(LoanPeriodRecord record) throws MeedlException {
        if (record == null){
            log.error("Loan period record must not be null - {}. ",record);
            throw new MeedlException("Loan period record must not be null.");
        }
        if (record.getLoanAmountOutstanding() == null) {
            log.error("Loan Amount Outstanding must not be null.");
            throw new MeedlException("Loan Amount Outstanding must not be null.");
        }
        if (record.getLoanAmountOutstanding().compareTo(BigDecimal.ZERO) < 0) {
            log.error("In validateLoanPeriodRecord Method. Loan Amount Outstanding must not be negative.");
            throw new MeedlException("Loan Amount Outstanding must not be negative.");
        }
        if (record.getDaysHeld() < 0) {
            log.error("Days Held must not be negative.");
            throw new MeedlException("Days Held must not be negative.");
        }
    }

    private void validateInterestRate(int interestRate, String name) throws MeedlException {
        if (interestRate < 0) {
            log.error("{} must not be negative. In validate interest rate.",name);
            throw new MeedlException(name+" must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate.", name );
            throw new MeedlException(name+" must not exceed 100.");
        }
    }

    private void validateInterestRate(double interestRate, String name) throws MeedlException {
        if (interestRate < 0) {
            log.error("{} must not be negative. In validate interest rate as double",name);
            throw new MeedlException(name+" must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate. as double", name );
            throw new MeedlException(name+" must not exceed 100.");
        }
    }

    public void validateAmount(BigDecimal amount, String name) throws MeedlException {
        if (amount == null) {
            log.error( "{} must not be null. In validate amount", name);
            throw new MeedlException(name + " must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.error( "{} must not be negative. In validate amount", name);
            throw new MeedlException(name + " must not be negative.");
        }
    }


}
