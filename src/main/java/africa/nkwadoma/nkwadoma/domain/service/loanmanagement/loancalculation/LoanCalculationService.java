package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class LoanCalculationService implements LoanCalculationUseCase {
    @Override
    public List<RepaymentHistory> sortRepaymentsByDateTimeDescending(List<RepaymentHistory> repayments) throws MeedlException {
        log.info("Started the sorting ");
        if (repayments == null) {
            log.warn("Repayments was null in the sorting method");
            return Collections.emptyList();
        }

        log.info("Repayments are not empty and the sorting has started. The number of the repayment is :{}", repayments.size());
        for (RepaymentHistory repayment : repayments) {
            if (repayment == null) {
                log.warn("Repayment history cannot be null");
                throw new MeedlException("Repayment history cannot be null");
            }
            log.info("Date of this repayment is : {}", repayment.getPaymentDateTime());
            if (repayment.getPaymentDateTime() == null) {
                log.warn("Payment date cannot be null");
                throw new MeedlException("Payment date cannot be null");
            }
        }

        repayments.sort(Comparator.comparing(RepaymentHistory::getPaymentDateTime).reversed());

        return repayments;
    }



}
