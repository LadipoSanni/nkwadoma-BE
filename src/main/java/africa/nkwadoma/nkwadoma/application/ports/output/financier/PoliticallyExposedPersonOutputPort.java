package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.PoliticallyExposedPerson;

public interface PoliticallyExposedPersonOutputPort {
    PoliticallyExposedPerson save(PoliticallyExposedPerson politicallyExposedPerson) throws MeedlException;

    PoliticallyExposedPerson findById(String politicallyExposedPersonId) throws MeedlException;

    void deleteById(String politicallyExposedPersonId) throws MeedlException;
}
