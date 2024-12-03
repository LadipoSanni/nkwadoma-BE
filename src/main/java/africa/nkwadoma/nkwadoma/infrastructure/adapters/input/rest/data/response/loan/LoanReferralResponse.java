package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanReferralResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String referredBy;
    private String cohortName;
    private String loaneeImage;
    private BigDecimal loanAmountRequested;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private LocalDate cohortStartDate;
    private String programName;
    private LoanReferralStatus loanReferralStatus;
}
