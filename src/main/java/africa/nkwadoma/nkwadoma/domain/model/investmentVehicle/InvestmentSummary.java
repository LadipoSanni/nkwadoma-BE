package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
public class InvestmentSummary {
    private String id;
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private String fundManager;
    private LocalDate dateInvested;
    private BigDecimal amountInvested;
    private BigDecimal netAssetValue;
    private double netAssetValueInPercent;
    private BigDecimal talentFunded;
    private double percentageOfPortfolio;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal incomeEarned;
    private IncomeInterval incomeInterval;
    private Set<InvestmentVehicleDesignation> designations;
    private OperationStatus operationStatus;
    private CouponDistributionStatus couponDistributionStatus;
    private VehicleClosure vehicleClosureStatus;
    private InvestmentVehicleVisibility investmentVehicleVisibility;
    private String mandate;
    private Float interestRateOffered;
    private InvestmentVehicleMode fundRaisingStatus;
    private InvestmentVehicleMode deployingStatus;
    private BigDecimal minimumInvestmentAmount;
}
