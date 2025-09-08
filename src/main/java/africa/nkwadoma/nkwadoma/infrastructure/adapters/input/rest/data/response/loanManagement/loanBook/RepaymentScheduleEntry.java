package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
public class RepaymentScheduleEntry {

    private LocalDate repaymentDate;
    private BigDecimal principalAmount;
    private BigDecimal expectedMonthlyAmount;
    private BigDecimal totalAmountRepaid;
    private BigDecimal principalPayment;
    private BigDecimal interest;
    private BigDecimal amountOutstanding;

}
