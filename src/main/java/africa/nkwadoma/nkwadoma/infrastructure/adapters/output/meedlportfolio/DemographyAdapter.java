package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemographyAdapter implements DemographyOutputPort {





    @Override
    public Demography save(Demography demography) {
        return null;
    }
}
