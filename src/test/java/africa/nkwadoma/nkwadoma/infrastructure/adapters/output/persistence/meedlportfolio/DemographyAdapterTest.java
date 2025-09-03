package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.meedlportfolio;


import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class DemographyAdapterTest {

    private Demography demography;
    @Autowired
    private DemographyOutputPort demographyOutputPort;


    @BeforeAll
    void setUp() {
        demography = TestData.buildDemography();
    }



    @Order(1)
    @Test
    void saveDemography() {
        Demography savedDemography = demographyOutputPort.save(demography);
        assertNotNull(savedDemography);
    }

}
