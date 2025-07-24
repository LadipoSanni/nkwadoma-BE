package africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface InvestmentVehicleUseCase {

    InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException;

    InvestmentVehicle viewInvestmentVehicleDetails(String investmentVehicleId, String userId) throws MeedlException;

    InvestmentVehicle viewInvestmentVehicleDetailsViaLink(String investmentVehicleLink) throws MeedlException;

    String deleteInvestmentVehicle(String  investmentId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicle(String userId,int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> searchInvestmentVehicle(String userId,InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicle investmentVehicle, String sortField, String userId) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleVisibility(String investmentVehicleId, InvestmentVehicleVisibility investmentVehicleVisibility, List<Financier> financiers) throws MeedlException;

    InvestmentVehicle setInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle) throws MeedlException;

    Page<InvestmentVehicle> viewAllInvestmentVehicleInvestedIn(String userId,String financierId, InvestmentVehicleType investmentVehicleType, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> searchMyInvestment(String userId, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException;

    FundStakeHolder viewFundStakeHolders();
}
