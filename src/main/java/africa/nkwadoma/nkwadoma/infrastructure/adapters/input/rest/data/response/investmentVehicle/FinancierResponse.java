package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleDesignation;
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
