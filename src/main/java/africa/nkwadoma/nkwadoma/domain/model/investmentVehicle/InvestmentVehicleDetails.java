package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
public class InvestmentVehicleDetails {
    private String investmentVehicleName;
    private InvestmentVehicleType investmentVehicleType;
    private LocalDate dateInvested;
    private BigDecimal amountInvested;
    private BigDecimal netAssetValue;
    private double percentageOfPortfolio;
    private LocalDate investmentStartDate;
    private LocalDate maturityDate;
    private InvestmentVehicleDesignation designation;
    private BigDecimal incomeEarned;
    private OperationStatus operationStatus;
    private CouponDistributionStatus couponDistributionStatus;
    private VehicleClosure vehicleClosureStatus;
    private InvestmentVehicleVisibility vehicleVisibilityStatus;
}
