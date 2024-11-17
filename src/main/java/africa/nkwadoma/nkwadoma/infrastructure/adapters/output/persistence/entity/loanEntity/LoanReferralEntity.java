package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
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
    @ManyToOne
    private LoaneeEntity loaneeEntity;
    @Enumerated(EnumType.STRING)
    private LoanReferralStatus loanReferralStatus;
}
