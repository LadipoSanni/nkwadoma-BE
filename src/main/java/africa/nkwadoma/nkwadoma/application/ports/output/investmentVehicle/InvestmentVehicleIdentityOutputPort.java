package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;

public interface InvestmentVehicleIdentityOutputPort {
    InvestmentVehicleIdentity save(InvestmentVehicleIdentity capitalGrowth) throws MiddlException;

    InvestmentVehicleIdentity findById(String id) throws MiddlException;

}
