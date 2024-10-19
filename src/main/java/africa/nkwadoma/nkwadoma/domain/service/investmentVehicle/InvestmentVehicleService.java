package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;

    @Override
    public InvestmentVehicle createOrUpdateInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    @Override
    public void deleteInvestmentVehicle(String investmentId) {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentId);
    }

    @Override
    public InvestmentVehicle viewInvestmentVehicleDetails(String id) throws MeedlException {
        return investmentVehicleOutputPort.findById(id);
    }


    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicle(int pageSize , int pageNumber) {
        return investmentVehicleOutputPort.findAllInvestmentVehicle(pageSize,pageNumber);
    }


}
