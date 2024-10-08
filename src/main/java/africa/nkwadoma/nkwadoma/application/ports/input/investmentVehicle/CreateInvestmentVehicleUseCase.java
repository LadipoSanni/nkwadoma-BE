package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;

import java.util.List;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicleIdentity createInvestmentVehicle(InvestmentVehicleIdentity investmentVehicleIdentity) throws MiddlException;

    InvestmentVehicleIdentity updateInvestmentVehicle(InvestmentVehicleIdentity foundInvestmentVehicle) throws MiddlException;

    InvestmentVehicleIdentity viewInvestmentVehicleDetails(String investmentId) throws MiddlException;

    List<InvestmentVehicleIdentity> viewAllInvestmentVehicles();
}
