package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;

public interface InvestmentVehicleFinancierOutputPort {
    InvestmentVehicleFinancier save(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException;

    InvestmentVehicleFinancier findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId) throws MeedlException;

    void deleteInvestmentVehicleFinancier(String id) throws MeedlException;
}
