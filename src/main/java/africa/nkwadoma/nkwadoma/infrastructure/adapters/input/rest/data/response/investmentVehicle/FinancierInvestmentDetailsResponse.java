package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentSummary;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class FinancierInvestmentDetailsResponse {
    private int numberOfInvestment;
    private BigDecimal totalAmountInvested;
    private BigDecimal totalIncomeEarned;
    private BigDecimal portfolioValue;
    private List<InvestmentSummary> investmentSummary;
}
