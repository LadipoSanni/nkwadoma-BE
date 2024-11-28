package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
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
    @NotBlank(message = "Loan product ID is required")
    private String loanProductId;
    @NotNull(message = "Status is required, ACCEPTED or DECLINED")
    private LoanRequestStatus status;
    private BigDecimal amountApproved;
    private String loanRequestDecision;
    private String declineReason;
}
