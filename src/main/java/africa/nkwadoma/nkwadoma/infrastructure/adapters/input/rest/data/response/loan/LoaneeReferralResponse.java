package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaneeReferralResponse {

    private String id;
    private Loanee loanee;
    private LoanReferralStatus loanReferralStatus;
}

