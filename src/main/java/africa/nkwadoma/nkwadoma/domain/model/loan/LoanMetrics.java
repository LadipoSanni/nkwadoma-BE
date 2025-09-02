package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class LoanMetrics {
    private String id;
    private String organizationId;
    private int loanRequestCount;
    private int loanDisbursalCount;
    private int loanReferralCount;
    private int loanOfferCount;
    private int uploadedLoanCount;
}
