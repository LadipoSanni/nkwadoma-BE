package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface InvestmentVehicleOutputPort {
    InvestmentVehicle save(InvestmentVehicle capitalGrowth) throws MeedlException;

    InvestmentVehicle findById(String id) throws MeedlException;

    void deleteInvestmentVehicle(String id) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize, int pageNumber);

    InvestmentVehicle findByNameExcludingDraftStatus(String name, InvestmentVehicleStatus status) throws MeedlException;

    List<InvestmentVehicle>  searchInvestmentVehicle(String  name) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType investmentVehicleType, InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException;

    Page<InvestmentVehicle> findAllInvestmentVehicleBy(Integer pageSize, Integer pageNumber, InvestmentVehicleType investmentVehicleType, InvestmentVehicleStatus investmentVehicleStatus, FundRaisingStatus fundRaisingStatus);
}
