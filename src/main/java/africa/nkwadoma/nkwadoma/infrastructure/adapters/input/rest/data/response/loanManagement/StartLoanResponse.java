package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StartLoanResponse {
    private LocalDateTime startDate;
    private String loanAccountId;
    private LoanStatus loanStatus;
}
