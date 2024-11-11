package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.UserIdentityRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AddLoaneeToCohortRequest {
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentityRequest loanee;
    private LoaneeLoanDetailRequest loaneeLoanDetail;
}
