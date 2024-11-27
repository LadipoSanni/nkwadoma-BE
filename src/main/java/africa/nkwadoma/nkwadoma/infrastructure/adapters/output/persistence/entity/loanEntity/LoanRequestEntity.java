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
    private String cohortId;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private String loanRequestDecision;
    private String declineReason;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private String reasonForDecliningLoanRequest;
    @Enumerated(EnumType.STRING)
    private LoanRequestStatus status;
    private String loanReferralId;
    @OneToOne
    private LoaneeEntity loaneeEntity;
}
