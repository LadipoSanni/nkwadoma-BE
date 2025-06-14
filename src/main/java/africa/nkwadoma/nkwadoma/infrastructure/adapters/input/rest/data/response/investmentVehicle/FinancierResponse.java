package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FinancierResponse {
    private String id;
    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private BigDecimal totalAmountInvested;
    private NextOfKin nextOfKin;
    private List<InvestmentVehicleDesignation> investmentVehicleRole;
    private String organizationName;
    private FinancierUserIdentityResponse userIdentity;
    private String invitedBy;
    private List<InvestmentVehicle> investmentVehicles;
}
