package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class LoanDetailsResponse {

    private String firstName;
    private String lastName;
    private String programName;
    private String cohortName;
    private LocalDate startDate;
    private LocalDate offerDate;
    private BigDecimal deposit;
    private BigDecimal amountRequested;
    private BigDecimal amountApproved;
    private LocalDateTime requestedDate;
    private LocalDateTime createdDate;
    private String loanProductName;
}
