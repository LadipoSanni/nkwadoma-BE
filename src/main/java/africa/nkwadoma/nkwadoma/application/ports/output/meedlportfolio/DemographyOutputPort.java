package africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;

public interface DemographyOutputPort {
    Demography save(Demography demography) throws MeedlException;

    void deleteById(String demographyId) throws MeedlException;

    Demography findDemographyByName(String meedl) throws MeedlException;
}
