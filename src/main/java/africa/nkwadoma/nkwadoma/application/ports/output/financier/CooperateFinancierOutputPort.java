package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;

public interface CooperateFinancierOutputPort {
    CooperateFinancier save(CooperateFinancier cooperateFinancier) throws MeedlException;

    void delete(String id) throws MeedlException;

    CooperateFinancier findByUserId(String id) throws MeedlException;

    CooperateFinancier findById(String cooperateFinancierId) throws MeedlException;

    CooperateFinancier findByFinancierId(String cooperateFinancierId);
}
