package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBreakdownResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class EditLoaneeDetailResponse {

    private String loaneeId;
    private String cohortId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal initialDeposit;
    private List<LoanBreakdownResponse> loanBreakdowns;
}
