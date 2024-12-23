package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateInvestmentVehicleRequest {

    private String name;
    private InvestmentVehicleType investmentVehicleType;
    @Size( max = 2500, message = "Investment vehicle mandate must not exceed 2500 characters")
    private String mandate;
    private String sponsors;
    private int tenure;
    private BigDecimal size;
    private Float rate;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private String sponsor;
    private BigDecimal minimumInvestmentAmount;
}
