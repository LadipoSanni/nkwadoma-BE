package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FinancierDashboardResponse {
    private String id;
    private String name;
    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String email;
    private String phoneNumber;
    private String cooperateAdminEmail;
    private String cooperateAdminName;
    private String address;
    private NextOfKin nextOfKin;
    private String taxId;
    private String rcNumber;
    private int totalNumberOfInvestment;
    private String totalAmountInvested;
    private BigDecimal totalIncomeEarned;
    private BigDecimal portfolioValue;
    private List<InvestmentVehicleResponse> investmentVehicleResponses;

}
