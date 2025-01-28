package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanLifeCycle {


    private String firstName;
    private String lastName;
    private String programName;
    private String cohortName;
    private String loanProductName;
    private LocalDate startDate;
    private LocalDate offerDate;
    private BigDecimal deposit;
    private BigDecimal amountRequested;
    private LocalDateTime requestedDate;
    private LocalDateTime createdDate;
    private BigDecimal amountApproved;
}
