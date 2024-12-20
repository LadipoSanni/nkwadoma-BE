package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LoanReferralRequest {
    @NotBlank(message = ControllerConstant.LOAN_REFERRAL_ID_IS_REQUIRED)
    private String id;
    private String reasonForDeclining;
    @Pattern(regexp = ControllerConstant.LOAN_REFERRAL_STATUS_TYPE, message = "Loan referral status must be accepted or declined")
    private String loanReferralStatus;
}
