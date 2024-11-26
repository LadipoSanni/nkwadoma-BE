package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String referredBy;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private String loanRequestDecision;
    private LocalDateTime dateTimeApproved;
    private String reasonForDecliningLoanRequest;
    @Enumerated(EnumType.STRING)
    private LoanRequestStatus status;
    @OneToOne
    private LoaneeEntity loaneeEntity;
}
