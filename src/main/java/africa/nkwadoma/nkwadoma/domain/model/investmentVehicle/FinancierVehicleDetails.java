package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class FinancierVehicleDetails {
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    private LocalDateTime dateInvested;
    private BigDecimal amountInvested;
    private BigDecimal netAssetValue;
    private double percentageOfPortfolio;
    private LocalDateTime investmentStartDate;
    private LocalDateTime maturityDate;
    private InvestmentVehicleDesignation designation;
    private String incomeEarned;
    private String vehicleOperationStatus;
    private String vehicleDistributionStatus;
    private String vehicleClosureStatus;
    private String vehicleVisibilityStatus;

}
