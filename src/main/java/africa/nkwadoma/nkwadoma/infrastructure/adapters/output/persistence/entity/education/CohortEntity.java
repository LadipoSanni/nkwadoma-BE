package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.persistence.*;
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
    private String name;
    private String programId;
    private String organizationId;
    @Size( max = 2500, message = "Cohort description must not exceed 2500 characters" )
    private String cohortDescription;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Enumerated(EnumType.STRING)
    private CohortStatus cohortStatus;
    @Enumerated(EnumType.STRING)
    private CohortType cohortType;
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
    private int numberOfLoanees = 0;
    private int stillInTraining = 0;
    private int numberOfDropout = 0;
    private int numberEmployed = 0;
    private int numberOfLoanRequest = 0;
    private int numberOfReferredLoanee = 0;
}
