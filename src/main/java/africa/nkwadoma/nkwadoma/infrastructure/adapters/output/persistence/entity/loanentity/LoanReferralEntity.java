package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanReferralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String reasonForDeclining;
    @OneToOne
    private CohortLoaneeEntity cohortLoanee;
    @Enumerated(EnumType.STRING)
    private LoanReferralStatus loanReferralStatus;
}
