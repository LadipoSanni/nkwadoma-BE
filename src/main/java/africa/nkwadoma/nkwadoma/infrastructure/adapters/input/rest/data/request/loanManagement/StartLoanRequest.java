package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartLoanRequest {
    private String loaneeId;
    private String loanOfferId;
}
