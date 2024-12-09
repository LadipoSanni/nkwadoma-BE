package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class LoaneeLoanDetailRequest {
    @NotNull(message = "InitialDeposit is required")
    private BigDecimal initialDeposit;
    private List<LoaneeLoanBreakdownRequest> loanBreakdown ;
}
