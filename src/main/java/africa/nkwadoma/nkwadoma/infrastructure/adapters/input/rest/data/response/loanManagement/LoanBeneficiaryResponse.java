package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LoanBeneficiaryResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String instituteName;
    private String performance;

    private double interestEarned;
    private BigDecimal amountDisbursed;
    private BigDecimal loanOutstanding;
}
