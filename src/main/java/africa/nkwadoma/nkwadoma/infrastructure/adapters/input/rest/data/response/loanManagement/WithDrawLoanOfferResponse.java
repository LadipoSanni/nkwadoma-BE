package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithDrawLoanOfferResponse {

    private String loanOfferId;
    private LoanOfferStatus loanOfferStatus;
}
