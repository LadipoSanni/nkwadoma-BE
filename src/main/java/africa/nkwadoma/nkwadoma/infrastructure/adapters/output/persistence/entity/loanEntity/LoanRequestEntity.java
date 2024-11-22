package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
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
    @ManyToOne
    private CohortEntity cohortEntity;
    private BigDecimal loanAmountRequested;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private String reasonForDecliningLoanRequest;
    @Enumerated(EnumType.STRING)
    private LoanRequestStatus status;
    @OneToOne
    private LoanReferralEntity loanReferralEntity;
    @OneToOne
    private LoaneeEntity loaneeEntity;
}
