package africa.nkwadoma.nkwadoma.domain.service.meedlPortfolio;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlPortfolio.MeedlPortfolioUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.MeedlPortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MeedlPortfolioService implements MeedlPortfolioUseCase {

    private final MeedlPortfolioOutputPort meedlPortfolioOutputPort;

    @Override
    public MeedlPortfolio viewMeedlPortfolio() {
        return meedlPortfolioOutputPort.findMeedlPortfolio();
    }
}
