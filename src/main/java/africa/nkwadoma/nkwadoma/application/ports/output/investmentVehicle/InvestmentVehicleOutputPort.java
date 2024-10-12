package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;

public interface InvestmentVehicleOutputPort {
    InvestmentVehicle save(InvestmentVehicle capitalGrowth) throws MiddlException;

    InvestmentVehicle findById(String id) throws MiddlException;

    void deleteInvestmentVehicle(String id);
}
