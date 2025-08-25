package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FinancierOutputPort {
    Financier save(Financier financier) throws MeedlException;

    Page<Financier> viewAllFinancier(Financier financier) throws MeedlException;

    Financier findFinancierByFinancierId(String financierId) throws MeedlException;

    Financier findFinancierByUserId(String userId) throws MeedlException;

    void delete(String financierId) throws MeedlException;

    Financier completeKyc(Financier financier) throws MeedlException;
    Page<Financier> search(String name, Financier financier) throws MeedlException;

    Financier findFinancierByEmail(String email) throws MeedlException;

    Financier findByIdentity(String id) throws MeedlException;

    Financier findFinancierByCooperateStaffUserId(String id) throws MeedlException;

    Financier findById(String id) throws MeedlException;
}
