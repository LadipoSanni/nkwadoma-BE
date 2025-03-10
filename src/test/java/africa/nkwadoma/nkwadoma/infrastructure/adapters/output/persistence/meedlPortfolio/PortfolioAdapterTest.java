package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.meedlPortfolio;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class PortfolioAdapterTest {

    private Portfolio portfolio;
    @Autowired
    private PortfolioOutputPort portfolioOutputPort;
    private String id;


    @BeforeAll
    void setUp() {
        portfolio = TestData.createMeedlPortfolio();
    }


    @Order(1)
    @Test
    void saveMeedlPortfolio() {
        try {
            Portfolio savedPortfolio = portfolioOutputPort.save(portfolio);
            id = savedPortfolio.getId();
            assertNotNull(savedPortfolio);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
    }

    @Order(2)
    @Test
    void findMeedlPortfolio() {
        Portfolio foundPortfolio = Portfolio.builder().build();
        try {
             foundPortfolio = portfolioOutputPort.findPortfolio(portfolio);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(id, foundPortfolio.getId());
    }


    @AfterAll
    void cleanUp(){
        portfolioOutputPort.delete(id);
    }
}
