package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SimulateRepaymentResponse {

    private BigDecimal monthlyRepayment;
    private BigDecimal totalRepayment;
    private BigDecimal totalInterestRepayment;
}
