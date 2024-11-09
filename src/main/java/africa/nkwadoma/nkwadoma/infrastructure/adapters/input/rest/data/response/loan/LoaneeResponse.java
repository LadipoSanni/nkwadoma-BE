package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoaneeResponse {
    private String id;
    private String organizationId;
    private String cohortId;
    private String programId;
    private String createdBy;
    private LocalDateTime createdAt;
    private UserIdentityResponse user;
    private LoaneeLoanDetailResponse loaneeLoanDetail;

}
