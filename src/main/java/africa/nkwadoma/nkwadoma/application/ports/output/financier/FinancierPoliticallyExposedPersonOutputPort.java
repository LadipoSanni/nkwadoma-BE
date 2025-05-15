package africa.nkwadoma.nkwadoma.application.ports.output.financier;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierPoliticallyExposedPerson;

import java.util.List;

public interface FinancierPoliticallyExposedPersonOutputPort {
    FinancierPoliticallyExposedPerson save(FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson) throws MeedlException;

    FinancierPoliticallyExposedPerson findById(String politicallyExposedPersonId) throws MeedlException;

    void deleteById(String politicallyExposedPersonId) throws MeedlException;

    List<FinancierPoliticallyExposedPerson> findAllByFinancierId(String financierId) throws MeedlException;
}
