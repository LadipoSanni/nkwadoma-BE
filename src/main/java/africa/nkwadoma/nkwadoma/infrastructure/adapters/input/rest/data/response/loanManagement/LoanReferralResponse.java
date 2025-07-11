package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.List;

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
    private boolean identityVerified;
    private LoanReferralStatus loanReferralStatus;
    private List<LoaneeLoanBreakDownResponse> loaneeLoanBreakdowns;
}
