package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;

    @Override
    public InvestmentVehicle createOrUpdateInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MiddlException {
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    @Override
    public void deleteInvestmentVehicle(String investmentId) {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentId);
    }


}
