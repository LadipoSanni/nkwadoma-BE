package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;

import java.util.List;

public interface FinancierBeneficialOwnerOutputPort {
    FinancierBeneficialOwner save(FinancierBeneficialOwner financierBeneficialOwner) throws MeedlException;

    FinancierBeneficialOwner findById(String beneficialOwnerId) throws MeedlException;

    void deleteById(String beneficialOwnerId) throws MeedlException;

    List<BeneficialOwner> findAllBeneficialOwner(String financierId) throws MeedlException;

    List<FinancierBeneficialOwner> findAllByFinancierId(String financierId) throws MeedlException;
}
