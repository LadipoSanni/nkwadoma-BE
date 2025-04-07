package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FinancierDashboardResponse {
    private String financierName;
    private FinancierType financierType;
    private String email;
    private String phoneNumber;
    private String address;
    private NextOfKin nextOfKin;
    private String taxId;
    private String rcNumber;
    private int numberOfInvestment;
    private String totalAmountInvested;
    private BigDecimal totalIncomeEarned;
    private BigDecimal portfolioValue;
    private List<InvestmentVehicle> investmentVehicles;

}
