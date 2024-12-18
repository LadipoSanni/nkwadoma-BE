package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class LoanReferralResponseRequest {
    private String id;
    @NotBlank(message = "Loan referral decision is required")
    private LoanReferralStatus loanReferralStatus;
    private String reason;
}
