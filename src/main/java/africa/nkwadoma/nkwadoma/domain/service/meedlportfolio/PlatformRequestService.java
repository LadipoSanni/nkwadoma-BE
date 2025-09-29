package africa.nkwadoma.nkwadoma.domain.service.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio.PlatformRequestUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PlatformRequestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlatformRequestService implements PlatformRequestUseCase{
    private final PlatformRequestOutputPort platformRequestOutputPort;

    @Override
    public Page<PlatformRequest> viewAll(PlatformRequest platformRequest) throws MeedlException {
       return platformRequestOutputPort.viewAll(platformRequest);
    }

}
