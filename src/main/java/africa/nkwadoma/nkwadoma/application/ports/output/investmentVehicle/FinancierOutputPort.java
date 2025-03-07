package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import org.springframework.data.domain.Page;

public interface FinancierOutputPort {
    Financier saveFinancier(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Financier findFinancierByFinancierId(String financierId) throws MeedlException;

    Financier findFinancierByUserId(String userId) throws MeedlException;

    void delete(String financierId) throws MeedlException;
}
