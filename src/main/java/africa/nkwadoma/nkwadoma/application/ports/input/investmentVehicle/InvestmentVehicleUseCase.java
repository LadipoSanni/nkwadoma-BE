package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
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

    Page<InvestmentVehicle> searchInvestmentVehicle(String userId,InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicle investmentVehicle, String sortField, String userId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleVisibility(String investmentVehicleId, InvestmentVehicleVisibility investmentVehicleVisibility, List<Financier> financiers) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleInvestedIn(String userId, InvestmentVehicleType investmentVehicleType, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> searchMyInvestment(String userId, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException;
}
