package africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;

public interface DemographyOutputPort {
    Demography save(Demography demography);
}
