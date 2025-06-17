package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Setter
@Getter
public class FinancierInvestmentResponse {

    private String investmentId;
    private String investmentVehicleName;
    private InvestmentVehicleType investmentVehicleType;
    private BigDecimal amountInvested;
    private LocalDate dateInvested;
    private BigDecimal incomeEarned;
    private BigDecimal netAssertValue;
    private double portfolioValue;

}
