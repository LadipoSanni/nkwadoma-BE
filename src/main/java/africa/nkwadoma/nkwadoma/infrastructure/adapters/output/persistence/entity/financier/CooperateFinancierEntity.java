package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CooperationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class CooperateFinancierEntity {

    @UuidGenerator
    @Id
    private String id;
    @ManyToOne
    private CooperationEntity cooperate;
    @ManyToOne
    private FinancierEntity financier;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
}
