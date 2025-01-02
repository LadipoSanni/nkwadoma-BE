package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanQueryResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String cohortName;
    private String programName;
    private BigDecimal amountRequested;
    private LocalDateTime offerDate;
    private LocalDateTime startDate;
    private BigDecimal initialDeposit;
    private LocalDate cohortStartDate;
}
