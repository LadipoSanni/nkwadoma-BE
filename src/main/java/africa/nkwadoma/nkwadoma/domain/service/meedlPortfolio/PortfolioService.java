package africa.nkwadoma.nkwadoma.domain.service.meedlPortfolio;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlPortfolio.PortfolioUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioService implements PortfolioUseCase {

    private final PortfolioOutputPort portfolioOutputPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final PortfolioMapper portfolioMapper;

    @Override
    public Portfolio viewPortfolio() throws MeedlException {
        Portfolio portfolio = Portfolio.builder().portfolioName("Meedl").build();
        portfolio = portfolioOutputPort.findPortfolio(portfolio);
        LoanMetricsProjection loanMetricsProjection = loanMetricsOutputPort.calculateAllMetrics();
        portfolioMapper.updateLoanMetricsOnPortfolio(portfolio,loanMetricsProjection);
        return portfolio;
    }
}
