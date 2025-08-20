package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import org.springframework.data.domain.Page;

public interface CooperateFinancierOutputPort {
    CooperateFinancier save(CooperateFinancier cooperateFinancier) throws MeedlException;

    void delete(String id) throws MeedlException;

    CooperateFinancier findByUserId(String id) throws MeedlException;

    CooperateFinancier findById(String cooperateFinancierId) throws MeedlException;

    CooperateFinancier findByFinancierId(String cooperateFinancierId);

    CooperateFinancier findCooperateFinancierByUserId(String id) throws MeedlException;

    CooperateFinancier findCooperateFinancierSuperAdminByCooperateName(String name) throws MeedlException;

    Page<CooperateFinancier> findAllFinancierInCooperationByCooperationId(String cooperationId, Financier financier) throws MeedlException;

    Page<CooperateFinancier> searchCooperationStaffByCooperationIdAndStaffName(String cooperationId, Financier financier) throws MeedlException;
}
