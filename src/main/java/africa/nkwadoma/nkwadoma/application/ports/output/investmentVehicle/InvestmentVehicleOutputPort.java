package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InvestmentVehicleOutputPort {
    InvestmentVehicle save(InvestmentVehicle capitalGrowth) throws MeedlException;

    InvestmentVehicle findById(String id) throws MeedlException;

    void deleteInvestmentVehicle(String id) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize , int pageNumber);
}
