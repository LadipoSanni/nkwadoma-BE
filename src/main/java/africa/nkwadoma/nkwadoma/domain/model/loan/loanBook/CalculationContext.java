package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.NUMBER_OF_DECIMAL_PLACES;

@Slf4j
@Getter
@Setter
@Builder
public class CalculationContext {
    private Cohort cohort;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private CohortLoanDetail cohortLoanDetail;
    private ProgramLoanDetail programLoanDetail;
    private OrganizationLoanDetail organizationLoanDetail;
    private List<RepaymentHistory> repaymentHistories;
    private BigDecimal previousTotalAmountPaid ;
    private BigDecimal previousTotalInterestIncurred ;
    private RepaymentHistory repaymentHistory;
    private LocalDateTime startDate;
    private BigDecimal totalInterestIncurredInAMonth;
    private BigDecimal totalInterestIncurred;
    private BigDecimal previousOutstandingAmount;

    public void setDefaultValues(){
        this.previousTotalAmountPaid = BigDecimal.ZERO;
        this.previousTotalInterestIncurred = BigDecimal.ZERO;
        this.totalInterestIncurred = BigDecimal.ZERO;
        this.totalInterestIncurredInAMonth =BigDecimal.ZERO;
        this.previousOutstandingAmount = null;
    }
    public void setPreviousAmountOutstanding() {
        if (ObjectUtils.isNotEmpty(this.getPreviousOutstandingAmount())) {
            log.info("Getting the previous amount outstanding as the previous in the calculation {}", this.getPreviousOutstandingAmount());
            this.setPreviousOutstandingAmount(decimalPlaceRoundUp(this.getPreviousOutstandingAmount()));
        }
        log.info("Getting the previous amount outstanding as amount received {}", this.getLoaneeLoanDetail().getAmountReceived());
        this.setPreviousOutstandingAmount(decimalPlaceRoundUp(this.getLoaneeLoanDetail().getAmountReceived()));
    }

    private BigDecimal decimalPlaceRoundUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
