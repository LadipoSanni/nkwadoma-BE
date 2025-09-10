package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanRequestDto {
    @NotBlank(message = "Loan request ID is required")
    private String loanRequestId;
    private String loanProductId;
    private BigDecimal amountApproved;
    @NotNull(message = "Loan decision is required, ACCEPTED or DECLINED")
    private LoanDecision loanRequestDecision;
    private String declineReason;
}
