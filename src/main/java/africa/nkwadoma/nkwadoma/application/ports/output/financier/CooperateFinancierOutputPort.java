package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;

import java.util.Optional;

public interface CooperateFinancierOutputPort {
    CooperateFinancier save(CooperateFinancier cooperateFinancier) throws MeedlException;

    void delete(String id) throws MeedlException;

    CooperateFinancier findByUserId(String id) throws MeedlException;

    CooperateFinancier findById(String cooperateFinancierId) throws MeedlException;

    CooperateFinancier findCooperateFinancierByUserId(String id) throws MeedlException;

    CooperateFinancier findCooperateFinancierSuperAdminByCooperateName(String name) throws MeedlException;
}
