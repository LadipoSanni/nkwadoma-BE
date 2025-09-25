package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LoanBreakdownRequest {

    private String loaneeLoanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount;
    private String currency;
}
