package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
    private BigDecimal portfolioValue;
    private List<InvestmentSummary> investmentSummaryList;

}
