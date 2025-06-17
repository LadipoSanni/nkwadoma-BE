package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class LoanReferralResponseRequest {
    @NotBlank(message = "Loan referral ID is required")
    private String id;
    private LoanReferralStatus loanReferralStatus;
    private String reason;
}
