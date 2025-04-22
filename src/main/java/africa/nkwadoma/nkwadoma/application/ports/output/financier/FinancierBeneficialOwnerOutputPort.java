package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;

public interface FinancierBeneficialOwnerOutputPort {
    FinancierBeneficialOwner save(FinancierBeneficialOwner financierBeneficialOwner) throws MeedlException;

    FinancierBeneficialOwner findById(String beneficialOwnerId) throws MeedlException;

    void deleteById(String beneficialOwnerId) throws MeedlException;
}
