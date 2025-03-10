package africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import org.springframework.data.domain.Page;

public interface FinancierUseCase {
    String inviteFinancier(Financier financier) throws MeedlException;

    Financier viewFinancierDetail(String financierId) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException;
}
