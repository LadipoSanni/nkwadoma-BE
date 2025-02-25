package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.InvestmentVehicleResponse;
import org.springframework.data.domain.*;

import java.util.List;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException;

    InvestmentVehicle viewInvestmentVehicleDetails(String id) throws MeedlException;

    void deleteInvestmentVehicle(String investmentId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicle(int pageSize, int pageNumber);

    List<InvestmentVehicle> searchInvestmentVehicle(String investmentVehicleName) throws MeedlException;

    InvestmentVehicle publishInvestmentVehicle(String investmentVehicleId) throws MeedlException;
}
