package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaneeReferralResponse {

    private String id;
    private LoaneeResponse loanee;
    private LoanReferralStatus loanReferralStatus;
}

