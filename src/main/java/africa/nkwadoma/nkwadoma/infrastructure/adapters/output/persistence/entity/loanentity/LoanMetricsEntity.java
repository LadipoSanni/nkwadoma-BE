package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanMetricsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String organizationId;
    private int loanRequestCount;
    private int loanDisbursalCount;
    private int loanReferralCount;
    private int loanOfferCount;
    private int uploadedLoanCount;
}
