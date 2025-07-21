package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CalculationEngine implements CalculationEngineUseCase {
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final int NUMBER_OF_DECIMAL_PLACE = 8;

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

    public BigDecimal calculateTotalAmountRepaid(List<RepaymentHistory> repayments) throws MeedlException {
        if (repayments == null || repayments.isEmpty()) {
            log.warn("Repayments was null when calculating total amount repaid");
            return BigDecimal.ZERO;
        }
        BigDecimal totalRepaymentAmount = BigDecimal.ZERO;

        for (RepaymentHistory repayment : repayments) {
            validationForCalculatingTotalAmountRepaid(repayment);
             totalRepaymentAmount = totalRepaymentAmount.add(repayment.getAmountPaid());
        }
        repayments.get(0).setTotalAmountRepaid(totalRepaymentAmount);
        return totalRepaymentAmount;
    }

    @Override
    public BigDecimal calculateTotalRepayment(
            List<RepaymentHistory> repaymentHistories,
            String loaneeId,
            String cohortId
    ) throws MeedlException {

        if (repaymentHistories == null || repaymentHistories.isEmpty()) {
            return BigDecimal.ZERO;
        }

//        RepaymentHistory lastRepayment = repaymentHistoryOutputPort.findLatestRepayment(loaneeId, cohortId);
        List<RepaymentHistory> previousRepaymentHistory = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loaneeId, cohortId);

        BigDecimal runningTotal = BigDecimal.ZERO;;
        if (previousRepaymentHistory != null && !previousRepaymentHistory.isEmpty()) {
            repaymentHistories = combinePreviousAndNewRepaymentHistory(previousRepaymentHistory, repaymentHistories);
        }
        repaymentHistories = sortRepaymentsByDateTimeAscending(repaymentHistories);

        for (RepaymentHistory repayment : repaymentHistories) {
            validateAmountRepaid(repayment);
            runningTotal = runningTotal.add(repayment.getAmountPaid());
            repayment.setTotalAmountRepaid(runningTotal);
        }
        log.info("The repayment histories after adding up total amount repaid {}", runningTotal);

        return runningTotal;
    }

    private static void validateAmountRepaid(RepaymentHistory repayment) throws MeedlException {
        if (ObjectUtils.isEmpty(repayment) || ObjectUtils.isEmpty(repayment.getAmountPaid())) {
            log.error("Repayment does not have amount paid ---- {}", repayment);
            throw new MeedlException("Repayment amount must be provided.");
        }
    }

    public static List<RepaymentHistory> combinePreviousAndNewRepaymentHistory(
            List<RepaymentHistory> previousRepaymentHistories,
            List<RepaymentHistory> newRepaymentHistories
    ) {
        List<RepaymentHistory> mergedList = new ArrayList<>(previousRepaymentHistories.size() + newRepaymentHistories.size());
        mergedList.addAll(previousRepaymentHistories);
        mergedList.addAll(newRepaymentHistories);
        return mergedList;
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
