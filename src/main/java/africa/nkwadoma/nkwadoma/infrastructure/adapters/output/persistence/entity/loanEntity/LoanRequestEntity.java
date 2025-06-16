package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestEntity {
    @Id
    private String id;
    private String referredBy;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    @Enumerated(EnumType.STRING)
    private LoanDecision loanRequestDecision;
    private String declineReason;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    private LoanRequestStatus status;
    @ManyToOne
    private LoaneeEntity loaneeEntity;
}
