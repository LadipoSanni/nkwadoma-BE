package africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import org.springframework.data.domain.Page;

public interface PlatformRequestUseCase {
    Page<PlatformRequest> viewAll(PlatformRequest platformRequest) throws MeedlException;
}
