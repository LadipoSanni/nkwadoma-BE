package africa.nkwadoma.nkwadoma.domain.service.meedlPortfolio;


import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;
    @Mock
    private PortfolioOutputPort portfolioOutputPort;
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
            portfolio = portfolioService.viewPortfolio();
        }catch (MeedlException meedlException) {
            log.info(meedlException.getMessage());
        }
        assertNotNull(portfolio);
    }

}
