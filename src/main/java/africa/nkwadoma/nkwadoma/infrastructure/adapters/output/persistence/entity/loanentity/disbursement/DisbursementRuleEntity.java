package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
public class DisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private DisbursementInterval interval;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "disbursement_rule_entity_percentage_distribution",
            joinColumns = @JoinColumn(name = "disbursement_rule_entity_id")
    )
    @Column(name = "percentage_distribution")
    private List<Double> percentageDistribution;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "disbursement_rule_entity_distribution_dates",
            joinColumns = @JoinColumn(name = "disbursement_rule_entity_id")
    )
    @Column(name = "distribution_dates")
    private List<LocalDateTime> distributionDates;
    private LocalDateTime dateUpdated;
//    private LocalDateTime endDate;
    private LocalDateTime dateCreated;
    private int numberOfTimesApplied;
    private int numberOfTimesAdjusted;
    private int numberOfUsage;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
}
