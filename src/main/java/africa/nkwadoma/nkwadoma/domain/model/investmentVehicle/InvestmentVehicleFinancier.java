package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleRole;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

import java.util.List;

public class InvestmentVehicleFinancier {

    private List<OrganizationIdentity> organizations;
    private List<UserIdentity> individuals;
    private InvestmentVehicleRole investmentVehicleRole;

}
