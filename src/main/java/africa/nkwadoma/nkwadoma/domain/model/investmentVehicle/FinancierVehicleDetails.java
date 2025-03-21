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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class FinancierVehicleDetails {
    private int numberOfInvestment;
    private BigDecimal totalAmountInvested;
    private BigDecimal totalIncomeEarned;
    private int portfolioValue;
    private List<InvestmentVehicleDetails> investmentVehicleDetailsList;

}
