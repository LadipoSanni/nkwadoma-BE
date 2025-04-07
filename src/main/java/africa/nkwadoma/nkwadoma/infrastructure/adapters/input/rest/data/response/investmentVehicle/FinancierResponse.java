package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinancierResponse {
    private String id;
    private FinancierType financierType;
    private NextOfKin nextOfKin;
    private List<InvestmentVehicleDesignation> investmentVehicleRole;
    private String organizationName;
    private FinancierUserIdentityResponse userIdentity;
    private String invitedBy;
    private List<InvestmentVehicle> investmentVehicles;
}
