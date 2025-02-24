package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestorInvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestorInvestmentVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestorInvestmentVehicleAdapter implements InvestorInvestmentVehicleOutputPort {
    private final InvestorInvestmentVehicleRepository investorInvestmentVehicleRepository;
    @Override
    public void addInvestorToVehicle(String investmentVehicleId, String investorId) {

        // TODO: Implement this method
    }
}
