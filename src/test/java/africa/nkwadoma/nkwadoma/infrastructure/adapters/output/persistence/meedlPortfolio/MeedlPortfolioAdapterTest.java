package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.meedlPortfolio;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.MeedlPortfolioOutputPort;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class MeedlPortfolioAdapterTest {

    private MeedlPortfolio meedlPortfolio;
    @Autowired
    private MeedlPortfolioOutputPort meedlPortfolioOutputPort;
    private String id;


    @BeforeAll
    void setUp() {
        meedlPortfolio = TestData.createMeedlPortfolio();
    }


    @Order(1)
    @Test
    void saveMeedlPortfolio() {
        try {
            MeedlPortfolio savedMeedlPortfolio = meedlPortfolioOutputPort.save(meedlPortfolio);
            id = savedMeedlPortfolio.getId();
            assertNotNull(savedMeedlPortfolio);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
    }


    @AfterAll
    void cleanUp(){
        meedlPortfolioOutputPort.delete(id);
    }
}
