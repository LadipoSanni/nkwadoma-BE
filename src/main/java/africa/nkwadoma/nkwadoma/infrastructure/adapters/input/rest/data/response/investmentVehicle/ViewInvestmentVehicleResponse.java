package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ViewInvestmentVehicleResponse {

    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private BigDecimal size;
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
}
