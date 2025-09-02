package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class AllLoanOfferResponse {

    private String id;
    private BigDecimal amountRequested;
    private BigDecimal amountApproved;
    private LocalDate dateOffered;
    private String loanProductName;
    private String firstName;
    private String lastName;
    private LoanDecision loanOfferResponse;
    private String status;
}
