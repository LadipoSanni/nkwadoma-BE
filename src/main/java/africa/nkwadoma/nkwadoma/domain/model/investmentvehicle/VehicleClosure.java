package africa.nkwadoma.nkwadoma.domain.model.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class VehicleClosure {

    private String id;
    private InvestmentVehicleMode recollectionStatus;
    private CapitalDistribution capitalDistribution;
    private String maturity;
}
