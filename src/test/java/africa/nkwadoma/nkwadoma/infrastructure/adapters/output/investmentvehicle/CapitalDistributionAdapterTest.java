package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CapitalDistributionOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CapitalDistribution;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CapitalDistributionAdapterTest {

    private CapitalDistribution capitalDistribution;
    private String capitalId;
    @Autowired
    private CapitalDistributionOutputPort capitalDistributionOutputPort;


    @BeforeAll
    void setUp() {
        capitalDistribution = TestData.buildCapitalDistribution();
    }

    @Test
    void saveCapitalDistribution() {
        CapitalDistribution savedCapitalDistribution = CapitalDistribution.builder().build();
        try{
            savedCapitalDistribution = capitalDistributionOutputPort.save(capitalDistribution);
            capitalId = savedCapitalDistribution.getId();
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(savedCapitalDistribution);
    }


    @AfterAll
    void tearDown() throws MeedlException {
        capitalDistributionOutputPort.deleteById(capitalId);
    }

}
