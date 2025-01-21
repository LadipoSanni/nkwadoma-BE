package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private LoanOfferStatus loanOfferStatus;
    @OneToOne
    private LoanRequestEntity loanRequest;
    @ManyToOne
    private LoanProductEntity loanProduct;
    @ManyToOne
    private LoaneeEntity loanee;
    private LocalDateTime dateTimeOffered;
    private LocalDateTime dateTimeAccepted;
    private LoanDecision loaneeResponse;
}
