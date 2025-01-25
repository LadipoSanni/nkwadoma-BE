package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
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
    private String referredBy;
    private LoanRequestStatus status;
    private String image;
    private int creditScore;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private String alternateEmail;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private LocalDateTime createdDate;
    private LocalDateTime dateTimeOffered;
    private BigDecimal tuitionAmount;
    private NextOfKinResponse nextOfKin;
    private LoaneeLoanBreakDownResponse loaneeLoanBreakDowns;
}
