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
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private LocalDate dateInvested;
    private BigDecimal amountInvested;
    private BigDecimal netAssetValue;
    private double netAssetValueInPercent;
    private double percentageOfPortfolio;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal incomeEarned;
    private Set<InvestmentVehicleDesignation> designations;
    private OperationStatus operationStatus;
    private CouponDistributionStatus couponDistributionStatus;
    private VehicleClosure vehicleClosureStatus;
    private InvestmentVehicleVisibility investmentVehicleVisibility;
}
