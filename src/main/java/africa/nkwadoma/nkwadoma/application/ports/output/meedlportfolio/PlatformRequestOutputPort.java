package africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;

public interface PlatformRequestOutputPort {
    PlatformRequest save(PlatformRequest platformRequest) throws MeedlException;
}
