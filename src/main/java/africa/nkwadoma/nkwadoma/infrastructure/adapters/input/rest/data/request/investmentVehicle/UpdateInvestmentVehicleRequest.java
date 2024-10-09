package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateInvestmentVehicleRequest {


    private String name;
    private String investmentVehicleType;
    private String mandate;
    private String sponsors;
    private String tenure;
    private Float rate;

}
