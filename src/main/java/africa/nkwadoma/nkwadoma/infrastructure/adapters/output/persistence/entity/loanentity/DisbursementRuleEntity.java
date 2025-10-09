package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class DisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private DisbursementInterval interval;
    @ElementCollection
    @CollectionTable(
            name = "disbursement_rule_entity_percentage_distribution",
            joinColumns = @JoinColumn(name = "disbursement_rule_entity_id")
    )
    @Column(name = "percentage_distribution")
    private List<Double> percentageDistribution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime dateCreated;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
}
