package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoaneeLoanAggregateResponse {

    private String loaneeId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal historicalDebt;
    private BigDecimal amountOutStanding;
    private int numberOfLoans;
}
