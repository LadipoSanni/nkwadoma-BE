package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;

public interface BeneficialOwnerOutputPort {
    BeneficialOwner save(BeneficialOwner beneficialOwner) throws MeedlException;

    BeneficialOwner findById(String beneficialOwnerId) throws MeedlException;

    void deleteById(String beneficialOwnerId) throws MeedlException;

}
