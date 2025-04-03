package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InvestmentVehicleVisibilityRequest {

    private String investmentVehicleId;
    private InvestmentVehicleVisibility visibility;
    private List<Financier> financiers;
}
