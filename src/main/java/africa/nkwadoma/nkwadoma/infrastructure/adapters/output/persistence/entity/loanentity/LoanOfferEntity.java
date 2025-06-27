package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private LoanOfferStatus loanOfferStatus;
    @ManyToOne
    private LoanProductEntity loanProduct;
    private LocalDateTime dateTimeOffered;
    private LocalDateTime dateTimeAccepted;
    private BigDecimal amountApproved;
    @Enumerated(EnumType.STRING)
    private LoanDecision loaneeResponse;
}
