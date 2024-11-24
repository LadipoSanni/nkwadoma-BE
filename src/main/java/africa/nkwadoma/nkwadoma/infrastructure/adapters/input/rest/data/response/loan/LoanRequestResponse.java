package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
public class LoanRequestResponse {
    private String id;
    private String referredBy;
    private String firstName;
    private String lastName;
    private BigDecimal loanAmountRequested;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private LocalDate cohortStartDate;
    private String programName;
}
