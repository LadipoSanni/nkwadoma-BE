package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleIdentityOutputPort investmentVehicleIdentityOutputPort;

    @Override
    public InvestmentVehicleIdentity createInvestmentVehicle(InvestmentVehicleIdentity investmentVehicleIdentity) throws MiddlException {
        return investmentVehicleIdentityOutputPort.save(investmentVehicleIdentity);
    }

}
