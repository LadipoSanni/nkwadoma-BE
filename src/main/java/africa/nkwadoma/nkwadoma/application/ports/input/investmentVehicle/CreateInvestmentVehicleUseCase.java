package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicle createOrUpdateInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException;
    InvestmentVehicle viewInvestmentVehicleDetails(String id) throws MeedlException;
    void deleteInvestmentVehicle(String investmentId) throws MeedlException;
}
