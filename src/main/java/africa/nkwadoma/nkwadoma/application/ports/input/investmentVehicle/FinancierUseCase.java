package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FinancierUseCase {
    String inviteFinancier(List<Financier> financier) throws MeedlException;

    Financier viewFinancierDetail(String financierId) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancierInInvestmentVehicleByActivationStatus(Financier financier) throws MeedlException;

    Page<Financier> search(String name, int pageNumber, int pageSize) throws MeedlException;

    void updateFinancierStatus(Financier financier);

    Financier investInVehicle(Financier financier) throws MeedlException;

    Financier completeKyc(Financier financier) throws MeedlException;

    Financier findFinancierByCooperationId(String cooperationId) throws MeedlException;

    Optional<InvestmentVehicle> accessInvestmentVehicle(String investmentVehicleId, String financierId) throws MeedlException;
}
