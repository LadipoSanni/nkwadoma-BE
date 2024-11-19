package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String programId;
    private String cohortDescription;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Enumerated(EnumType.STRING)
    private CohortStatus cohortStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    @OneToOne
    private LoanDetailEntity loanDetail;
    private Integer numberOfLoanees = 0;
    private Integer numberOfReferredLoanee = 0;
}
