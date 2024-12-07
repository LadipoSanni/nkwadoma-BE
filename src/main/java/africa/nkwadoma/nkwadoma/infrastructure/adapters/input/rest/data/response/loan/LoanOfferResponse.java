package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class LoanOfferResponse {

    private String id;
    private LoanRequestResponse loanRequestResponse;
    private LoanOfferStatus loanOfferStatus;
    private LoaneeResponse loaneeResponse;
    private LoanProductResponse loanProductResponse;
    private LocalDateTime dateTimeOffered;
}
