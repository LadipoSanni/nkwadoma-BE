package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;


import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CooperateFinancierAdapterTest {


    @Autowired
    private CooperateFinancierOutputPort cooperateFinancierOutputPort;
    private CooperateFinancier cooperateFinancier;
    private Financier financier;
    private UserIdentity userIdentity;
    private Cooperation cooperate;


    @BeforeAll
    void setUp() {
        userIdentity = TestData.createTestUserIdentity("financier@grr.la");
        financier = TestData.buildFinancierIndividual(userIdentity);
        cooperate = TestData.buildCooperation("Nepo BABY!!!!");
        cooperateFinancier = TestData.buildCoperateFinancier(financier,cooperate);
    }

    @Test
    void saveNullCooperateFinancier() {
        assertThrows(MeedlException.class,()-> cooperateFinancierOutputPort.save(null));
    }

    @Test
    void saveCooperateFinancierWithNullCooperate() {
        cooperateFinancier.setCooperate(null);
        assertThrows(MeedlException.class,()-> cooperateFinancierOutputPort.save(cooperateFinancier));
    }

    @Test
    void saveCooperateFinancierWithNullFinancier() {
        cooperateFinancier.setFinancier(null);
        assertThrows(MeedlException.class, ()-> cooperateFinancierOutputPort.save(cooperateFinancier));
    }

    @Test
    void saveCooperateFinancierWithNullActivationStatus() {
        cooperateFinancier.setActivationStatus(null);
        assertThrows(MeedlException.class, ()-> cooperateFinancierOutputPort.save(cooperateFinancier));
    }

    
}
