package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InvestmentVehicleFinancierOutputPort {
    InvestmentVehicleFinancier save(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException;
    //Todo below findByInvestmentVehicleIdAndFinancierId is a method to remove
    Optional<InvestmentVehicleFinancier> findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId) throws MeedlException;

    void deleteInvestmentVehicleFinancier(String id) throws MeedlException;

    Page<Financier> viewAllFinancierInAnInvestmentVehicle(String investmentVehicleId, Pageable pageRequest) throws MeedlException;

    Page<Financier> viewAllFinancierInAnInvestmentVehicle(String investmentVehicleId, ActivationStatus activationStatus, Pageable pageRequest) throws MeedlException;

    void deleteByInvestmentVehicleIdAndFinancierId(String investmentId, String id) throws MeedlException;

    List<InvestmentVehicleFinancier> findAllInvestmentVehicleFinancierInvestedIn(String financierId) throws MeedlException;

    List<InvestmentVehicleFinancier> findByAll(String investmentVehicleId, String financierId);
}
