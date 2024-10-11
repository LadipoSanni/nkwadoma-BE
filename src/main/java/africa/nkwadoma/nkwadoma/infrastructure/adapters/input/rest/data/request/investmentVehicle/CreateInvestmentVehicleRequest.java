package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
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
    private String tenure;
    private BigDecimal size;
    private Float rate;
}
