package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    private BigDecimal tuitionAmount;
    private LocalDate cohortStartDate;
    private String referredBy;
    private LoanRequestStatus status;
    private String image;
    private int creditScore;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private LocalDateTime createdDate;
    private LocalDateTime dateTimeOffered;
    private UserIdentityResponse userIdentity;
    private NextOfKinResponse nextOfKin;
    private List<LoaneeLoanBreakDownResponse> loaneeLoanBreakDowns;
}
