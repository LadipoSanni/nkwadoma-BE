package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.UserIdentityRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoaneeRequest {
    private String cohortId;
    private UserIdentityRequest userIdentity;
    private LoaneeLoanDetailRequest loaneeLoanDetail;
}
