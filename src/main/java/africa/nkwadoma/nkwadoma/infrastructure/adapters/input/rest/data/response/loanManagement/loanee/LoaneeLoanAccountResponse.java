package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.AccountStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LoaneeLoanAccountResponse {

    private String id;
    private AccountStatus accountStatus;
    private LoanStatus loanStatus;
    private String loaneeId;
}
