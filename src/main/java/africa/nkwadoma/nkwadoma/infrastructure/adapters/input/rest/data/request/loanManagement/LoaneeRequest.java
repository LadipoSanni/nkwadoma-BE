package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.UserIdentityRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaneeRequest {
    private String id;
    @NotBlank(message = "Cohort Id is required")
    private String cohortId;
    @NotNull(message = "Loanee Details is required")
    private UserIdentityRequest userIdentity;
    @NotNull(message = "Loanee LoanDetails is required")
    private LoaneeLoanDetailRequest loaneeLoanDetail;
}
