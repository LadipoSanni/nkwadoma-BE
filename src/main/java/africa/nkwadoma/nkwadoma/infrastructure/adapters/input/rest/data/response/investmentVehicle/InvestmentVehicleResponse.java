package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.enums.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class InvestmentVehicleResponse {

    private String id;
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private String mandate;
    private String sponsors;
    private String tenure;
    private BigDecimal size;
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
    private BigDecimal totalAmountInInvestmentVehicle;
    private BigDecimal amountRaised;
    private BigDecimal amountDisbursed;
    private BigDecimal amountAvailable;
    private BigDecimal totalIncomeGenerated;
    private BigDecimal netAssetValue;
}
