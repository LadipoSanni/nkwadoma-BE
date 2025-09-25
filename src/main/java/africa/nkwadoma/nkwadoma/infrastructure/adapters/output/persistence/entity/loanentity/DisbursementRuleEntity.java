package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Setter
@Getter
public class DisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private String query;
    private ActivationStatus activationStatus;
}
