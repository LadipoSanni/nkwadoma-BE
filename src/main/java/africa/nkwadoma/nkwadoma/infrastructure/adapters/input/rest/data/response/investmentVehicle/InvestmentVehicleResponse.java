package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import lombok.Getter;
import lombok.Setter;
import org.apache.james.mime4j.dom.datetime.DateTime;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvestmentVehicleResponse {

    private String id;
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private String mandate;
    private String sponsors;
    private int tenure;
    private BigDecimal size;
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
    private BigDecimal totalAmountInInvestmentVehicle =BigDecimal.ZERO;
    private BigDecimal amountRaised = BigDecimal.ZERO;
    private BigDecimal amountDisbursed = BigDecimal.ZERO;
    private BigDecimal amountAvailable = BigDecimal.ZERO;
    private BigDecimal totalIncomeGenerated;
    private BigDecimal netAssetValue;
    private LocalDate startDate;
    private String investmentVehicleLink;
    private InvestmentVehicleStatus investmentVehicleStatus;
    private BigDecimal minimumInvestmentAmount;
    private String fundManager;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private LocalDate lastUpdatedDate;
    private InvestmentVehicleVisibility investmentVehicleVisibility;

}
