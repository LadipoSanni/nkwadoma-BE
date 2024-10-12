package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicle createOrUpdateInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MiddlException;

}
