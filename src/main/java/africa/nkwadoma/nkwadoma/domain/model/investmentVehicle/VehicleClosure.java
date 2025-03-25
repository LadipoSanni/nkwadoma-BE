package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CapitalDistributionEntity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Builder
public class VehicleClosure {

    private String id;
    private InvestmentVehicleMode investmentVehicleMode;
    private CapitalDistribution capitalDistribution;
    private String maturity;
}
