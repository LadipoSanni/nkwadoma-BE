package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoaneeResponse {
    private String id;
    private String cohortId;
    private String createdBy;
    private int creditScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentityResponse userIdentity;
    private LoaneeLoanDetailResponse loaneeLoanDetail;
    private LoaneeStatus loaneeStatus;
}
