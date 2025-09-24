package africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InvestmentVehicleFinancierOutputPort {
    InvestmentVehicleFinancier save(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException;

    void deleteInvestmentVehicleFinancier(String id) throws MeedlException;

    Page<Financier> viewAllFinancierInAnInvestmentVehicle(String investmentVehicleId, ActivationStatus activationStatus, Pageable pageRequest) throws MeedlException;

    void deleteByInvestmentVehicleIdAndFinancierId(String investmentId, String id) throws MeedlException;

    List<InvestmentVehicleFinancier> findAllInvestmentVehicleFinancierInvestedIn(String financierId) throws MeedlException;

    List<InvestmentVehicleFinancier> findByAll(String investmentVehicleId, String financierId) throws MeedlException;

    boolean checkIfAnyFinancierHaveInvestedInVehicle(String investmentVehicleId) throws MeedlException;

    void removeFinancierAssociationWithInvestmentVehicle(String investmentVehicleId) throws MeedlException;

    boolean checkIfFinancierExistInVehicle(String investmentVehicleId) throws MeedlException;

    Page<InvestmentVehicleFinancier> findAllInvestmentVehicleFinancierInvestedIntoByUserId(String id, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicleFinancier> findAllInvestmentVehicleFinancierInvestedIntoByFinancierId(String finanacierId, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicleFinancier> searchFinancierInvestmentByInvestmentVehicleNameAndUserId(String investmentVehicleName, String id, int pageSize, int pageNumber) throws MeedlException;

    Page<InvestmentVehicleFinancier> searchFinancierInvestmentByInvestmentVehicleNameAndFinancierId(String investmentVehicleName, String id, int pageSize, int pageNumber) throws MeedlException;

    InvestmentVehicleFinancier findByFinancierIdAndInvestmentVehicleFinancierId(String id, String investmentVehicleFinancierId) throws MeedlException;

    int checkIfFinancierExistInVehicle(String financierId, String investmentVehicleId) throws MeedlException;

    Optional<InvestmentVehicleFinancier> findRecentInvestmentVehicleFinancierIsAddedTo(String financierId) throws MeedlException;
}
