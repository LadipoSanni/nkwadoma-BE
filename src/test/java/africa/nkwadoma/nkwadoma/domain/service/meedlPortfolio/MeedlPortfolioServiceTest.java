package africa.nkwadoma.nkwadoma.domain.service.meedlPortfolio;


import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.MeedlPortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MeedlPortfolioServiceTest {

    @InjectMocks
    private MeedlPortfolioService meedlPortfolioService;
    @Mock
    private MeedlPortfolioOutputPort portfolioOutputPort;
    private MeedlPortfolio meedlPortfolio;


    @BeforeEach
    void setUp() {
        meedlPortfolio = TestData.createMeedlPortfolio();
    }

    @Test
    void viewMeedlPortfolio() {
        when(portfolioOutputPort.findMeedlPortfolio())
                .thenReturn(meedlPortfolio);
        MeedlPortfolio portfolio = meedlPortfolioService.viewMeedlPortfolio();
        assertNotNull(portfolio);
    }

}
