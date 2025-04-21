package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private BigDecimal totalAvailableAmount=BigDecimal.ZERO;
    private BigDecimal amountRaised = BigDecimal.ZERO;
    private BigDecimal amountDisbursed = BigDecimal.ZERO;
    private BigDecimal amountAvailable = BigDecimal.ZERO;
    private BigDecimal totalIncomeGenerated;
    private BigDecimal netAssetValue;
    private LocalDate startDate;
    private LocalDateTime createdDate;
    private String investmentVehicleLink;
    private InvestmentVehicleStatus investmentVehicleStatus;
    private BigDecimal minimumInvestmentAmount;
    private String fundManager;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private LocalDateTime lastUpdatedDate;
    private InvestmentVehicleVisibility investmentVehicleVisibility;
    private InvestmentVehicleMode fundRaisingStatus;
    private InvestmentVehicleMode deployingStatus;


}
