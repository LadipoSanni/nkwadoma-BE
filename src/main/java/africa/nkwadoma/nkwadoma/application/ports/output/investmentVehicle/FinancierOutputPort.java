package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierDetails;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FinancierOutputPort {
    Financier save(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Financier findFinancierByFinancierId(String financierId) throws MeedlException;

//    FinancierDetails findFinancierDetailsByFinancierId(String financierId) throws MeedlException;

    Financier findFinancierByUserId(String userId) throws MeedlException;

    void delete(String financierId) throws MeedlException;

    Financier completeKyc(Financier financier) throws MeedlException;
    Page<Financier> search(String name, int pageNumber, int pageSize) throws MeedlException;
}
