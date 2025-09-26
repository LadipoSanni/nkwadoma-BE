package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanBreakdownRequest;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class EditLoaneeDetailRequest {

    private String loaneeId;
    private String cohortId;
    @Size( max = 255, message = "First name must not exceed 255 characters" )
    private String firstName;
    @Size( max = 255, message = "Last name must not exceed 255 characters" )
    private String lastName;
    private String email;
    private BigDecimal initialDeposit;
    private List<LoanBreakdownRequest> loanBreakdown;
}
