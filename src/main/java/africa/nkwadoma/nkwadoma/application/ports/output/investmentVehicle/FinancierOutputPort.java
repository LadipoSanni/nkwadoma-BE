package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import org.springframework.data.domain.Page;

public interface FinancierOutputPort {
    Financier saveFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinanciersInInvestmentVehicle(Financier financier) throws MeedlException;

    Financier findFinancierById(String financierId) throws MeedlException;

}
