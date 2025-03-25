package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
    private InvestmentVehicleMode investmentVehicleMode;
    @OneToOne
    private CapitalDistributionEntity capitalDistribution;
    private String maturity;
}
