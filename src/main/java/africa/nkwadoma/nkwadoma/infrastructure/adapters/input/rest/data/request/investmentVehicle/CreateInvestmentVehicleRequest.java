package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateInvestmentVehicleRequest {

    private String name;
    private String investmentVehicleType;
    private String mandate;
    private String sponsors;
    private int tenure;
    private BigDecimal size;
    private Float rate;
}
