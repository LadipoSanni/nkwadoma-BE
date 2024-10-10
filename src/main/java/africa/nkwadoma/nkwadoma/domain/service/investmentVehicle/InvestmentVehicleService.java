package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort1;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort1 investmentVehicleOutputPort1;

    @Override
    public InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MiddlException {
        return investmentVehicleOutputPort1.save(investmentVehicle);
    }

    @Override
    public InvestmentVehicle updateInvestmentVehicle(InvestmentVehicle foundInvestmentVehicle) throws MiddlException {
        return investmentVehicleOutputPort1.save(foundInvestmentVehicle);
    }


}
