package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanBreakdownRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class EditLoaneeDetailRequest {

    private String loaneeId;
    private String cohortId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal initialDeposit;
    private List<LoanBreakdownRequest> loanBreakdown;
}
