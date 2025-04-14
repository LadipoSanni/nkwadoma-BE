package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.ViewInvestmentVehicleRequest;
import org.springframework.data.domain.*;

public interface InvestmentVehicleOutputPort {
    InvestmentVehicle save(InvestmentVehicle capitalGrowth) throws MeedlException;

    InvestmentVehicle findById(String id) throws MeedlException;

    void deleteInvestmentVehicle(String id) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize, int pageNumber);

    InvestmentVehicle findByNameExcludingDraftStatus(String name, InvestmentVehicleStatus status) throws MeedlException;

    Page<InvestmentVehicle> searchInvestmentVehicle(String name, InvestmentVehicle investmentVehicle,
                                                    int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType investmentVehicleType, InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicle investmentVehicle, String sortField, String userId) throws MeedlException;
    Page<InvestmentVehicle> findAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException;


    Page<InvestmentVehicle> findAllInvestmentVehicleExcludingPrivate(String userId,int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicle> searchInvestmentVehicleExcludingPrivate(String id, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException;

}
