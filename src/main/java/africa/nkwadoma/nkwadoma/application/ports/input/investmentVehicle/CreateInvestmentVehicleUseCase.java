package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface CreateInvestmentVehicleUseCase {

    InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException;

    InvestmentVehicle viewInvestmentVehicleDetails(String id) throws MeedlException;

    void deleteInvestmentVehicle(String investmentId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicle(int pageSize, int pageNumber);

    List<InvestmentVehicle> searchInvestmentVehicle(String investmentVehicleName) throws MeedlException;

    InvestmentVehicle publishInvestmentVehicle(String investmentVehicleId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException;

    List<InvestmentVehicle> viewAllInvestmentVehicleByStatus(InvestmentVehicleStatus status) throws MeedlException;
}
