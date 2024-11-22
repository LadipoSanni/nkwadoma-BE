package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Column(unique = true)
    private String name;
    private String programId;
    @Size(max = 2500, message = "cohort description must no go beyond 2500")
    private String cohortDescription;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Enumerated(EnumType.STRING)
    private CohortStatus cohortStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    @Column(nullable = false)
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    @OneToOne
    private LoanDetailEntity loanDetail;
    private Integer numberOfLoanees = 0;
}
