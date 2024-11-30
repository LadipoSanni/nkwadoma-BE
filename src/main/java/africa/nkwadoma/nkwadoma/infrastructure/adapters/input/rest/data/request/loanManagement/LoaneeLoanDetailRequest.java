package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class LoaneeLoanDetailRequest {
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoaneeLoanBreakdownRequest> loanBreakdown ;
}
