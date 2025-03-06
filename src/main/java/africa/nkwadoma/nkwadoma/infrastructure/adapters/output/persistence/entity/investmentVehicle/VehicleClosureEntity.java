package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class VehicleClosureEntity {
    @Id
    private String id;
    private InvestmentVehicleMode investmentVehicleMode;
    @OneToOne
    private CapitalDistributionEntity capitalDistribution;
    private String maturity;
}
