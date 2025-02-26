package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestorInvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestorInvestmentVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestorInvestmentVehicleAdapter implements InvestorInvestmentVehicleOutputPort {
    private final InvestorInvestmentVehicleRepository investorInvestmentVehicleRepository;
//    @Override
    public void addInvestorToVehicle(InvestmentVehicle investmentVehicle, UserIdentity investor) {

        // TODO: Implement this method
    }
}
