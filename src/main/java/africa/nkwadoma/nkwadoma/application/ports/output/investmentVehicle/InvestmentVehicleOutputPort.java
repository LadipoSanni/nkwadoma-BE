package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import org.springframework.data.domain.*;

public interface InvestmentVehicleOutputPort {
    InvestmentVehicle save(InvestmentVehicle capitalGrowth) throws MeedlException;

    InvestmentVehicle findById(String id) throws MeedlException;

    void deleteInvestmentVehicle(String id) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize, int pageNumber);

    InvestmentVehicle findByName(String name) throws MeedlException;
}
