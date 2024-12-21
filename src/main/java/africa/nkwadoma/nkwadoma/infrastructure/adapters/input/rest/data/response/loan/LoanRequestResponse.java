package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
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
@Builder(toBuilder = true)
public class LoanRequestResponse {
    private String id;
    private String referredBy;
    private String firstName;
    private String lastName;
    private LoanRequestStatus status;
    private String image;
    private BigDecimal loanAmountRequested;
    private String alternateEmail;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private LocalDate cohortStartDate;
    private String programName;
    private String cohortName;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
    private UserIdentityResponse userIdentity;
    private NextOfKinResponse nextOfKin;
}
