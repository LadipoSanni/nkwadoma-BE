package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
@Setter
@Getter
@Entity
public class VehicleClosureEntity {
    @Id
    @UuidGenerator
    private String id;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleMode recollectionStatus;
    @OneToOne
    private CapitalDistributionEntity capitalDistribution;
    private String maturity;
}
