package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface InvestmentVehicleUseCase {

    InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException;

    InvestmentVehicle viewInvestmentVehicleDetails(String investmentVehicleId, String userId) throws MeedlException;

    String deleteInvestmentVehicle(String investmentId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicle(String userId,int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> searchInvestmentVehicle(String investmentVehicleName, InvestmentVehicleType investmentVehicleType, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus status) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType type, InvestmentVehicleStatus status) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicleType investmentVehicleType, InvestmentVehicleStatus investmentVehicleStatus, FundRaisingStatus fundRaisingStatus) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleVisibility(String investmentVehicleId, InvestmentVehicleVisibility investmentVehicleVisibility, List<Financier> financiers) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle) throws MeedlException;
}
