package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoanCalculationService implements LoanCalculationUseCase {
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;

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

    public BigDecimal calculateTotalAmountRepaidPerRepayment(List<RepaymentHistory> repayments) throws MeedlException {
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
    public BigDecimal calculateCurrentTotalAmountRepaid(
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
//        return repaymentHistories.stream()
//                .map(RepaymentHistory::getAmountPaid)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);


        for (RepaymentHistory repayment : repaymentHistories) {
            validateAmountRepaid(repayment);
            runningTotal = calculateTotalAmountRepaidPerRepayment(repayment, runningTotal);
kl
        }
        log.info("The repayment histories after adding up total amount repaid {}", runningTotal);

        return runningTotal;
    }

    private static BigDecimal calculateTotalAmountRepaidPerRepayment(RepaymentHistory repayment, BigDecimal runningTotal) {
        runningTotal = runningTotal.add(repayment.getAmountPaid());
        repayment.setTotalAmountRepaid(runningTotal);
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

}
