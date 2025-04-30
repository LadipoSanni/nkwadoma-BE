package africa.nkwadoma.nkwadoma.domain.service.meedlPortfolio;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlPortfolio.PortfolioUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioService implements PortfolioUseCase {

    private final PortfolioOutputPort portfolioOutputPort;

    @Override
    public Portfolio viewPortfolio() throws MeedlException {
        Portfolio portfolio = Portfolio.builder().portfolioName("Meedl").build();

        return portfolioOutputPort.findPortfolio(portfolio);
    }
}
