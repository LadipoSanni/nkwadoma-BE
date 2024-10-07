package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicleIdentity createInvestmentVehicle(InvestmentVehicleIdentity investmentVehicleIdentity) throws MiddlException;

    InvestmentVehicleIdentity updateInvestmentVehicle(InvestmentVehicleIdentity foundInvestmentVehicle) throws MiddlException;
}
