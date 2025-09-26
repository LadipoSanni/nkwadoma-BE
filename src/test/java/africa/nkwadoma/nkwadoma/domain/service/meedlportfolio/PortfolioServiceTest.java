package africa.nkwadoma.nkwadoma.domain.service.meedlportfolio;


import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;
    @Mock
    private PortfolioOutputPort portfolioOutputPort;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private PortfolioMapper portfolioMapper;
    private Portfolio portfolio;


    @BeforeEach
    void setUp() {
        portfolio = TestData.createMeedlPortfolio();
    }

    @Test
    void viewPortfolio() {
        try {
            when(portfolioOutputPort.findPortfolio(Mockito.any(Portfolio.class)))
                    .thenReturn(portfolio);
            LoanMetricsProjection loanMetricsProjection = Mockito.mock(LoanMetricsProjection.class);
            when(loanMetricsOutputPort.calculateAllMetrics()).thenReturn(loanMetricsProjection);
            portfolio = portfolioService.viewPortfolio();
        }catch (MeedlException meedlException) {
            log.info(meedlException.getMessage());
        }
        assertNotNull(portfolio);
    }

}
