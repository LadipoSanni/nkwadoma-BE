package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class LoanDisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private LoanEntity loanEntity;
    @ManyToOne
    private DisbursementRuleEntity disbursementRuleEntity;

    private String name;
    private String appliedBy;
    @Enumerated(EnumType.STRING)
    private DisbursementInterval interval;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "loan_disbursement_rule_percentage_distribution",
            joinColumns = @JoinColumn(name = "loan_disbursement_rule_entity_id")
    )
    @Column(name = "percentage_distribution")
    private List<Double> percentageDistribution;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "loan_disbursement_rule_distribution_dates",
            joinColumns = @JoinColumn(name = "loan_disbursement_rule_entity_id")
    )
    @Column(name = "distribution_dates")
    private List<LocalDateTime> distributionDates;
    @Enumerated(EnumType.STRING)
    private DisbursementRuleStatus disbursementRuleStatus;
    private LocalDateTime dateApplied;

    private int numberOfTimesAdjusted;
    private LocalDateTime dateLastAdjusted;
}
