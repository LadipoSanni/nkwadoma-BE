package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeLoanBreakDownResponse;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

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
    private LoanRequestStatus status;
    private String image;
    private int creditScore;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private BigDecimal tuitionAmount;
    private String alternateEmail;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private LocalDateTime createdDate;
    private LocalDateTime dateTimeOffered;
    private BigDecimal initialDeposit;
    private LocalDate cohortStartDate;
    private String programName;
    private String cohortName;
    private List<LoaneeLoanBreakDownResponse> loaneeLoanBreakdowns;
    private UserIdentityResponse userIdentity;
    private NextOfKinResponse nextOfKin;
}
