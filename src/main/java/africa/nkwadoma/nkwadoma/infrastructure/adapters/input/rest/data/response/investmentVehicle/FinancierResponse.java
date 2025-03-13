package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinancierResponse {
    private String id;
    private String organizationName;
    private UserIdentityResponse individual;
    private String invitedBy;
    private String investmentVehicleId;
    private FinancierType financierType;
    private List<InvestmentVehicleDesignation> investmentVehicleRole;
}
