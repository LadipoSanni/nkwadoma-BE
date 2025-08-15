package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;


import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
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

import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private CooperationOutputPort cooperationOutputPort;
    private String cooperateFinancierID;


    @BeforeAll
    void setUp() throws MeedlException {
        userIdentity = TestData.createTestUserIdentity("financier@grr.la");
        userIdentity.setRole(IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN);
        userIdentity = userIdentityOutputPort.save(userIdentity);
        financier = TestData.buildFinancierIndividual(userIdentity);
        financier = financierOutputPort.save(financier);
        cooperate = TestData.buildCooperation("NepoBABY");
        cooperate = cooperationOutputPort.save(cooperate);
        cooperateFinancier = TestData.buildCooperateFinancier(financier,cooperate);
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

    @Order(1)
    @Test
    void saveCooperateFinancierCooperation() {
        CooperateFinancier savedCooperateFinancier = null;
        try{
            savedCooperateFinancier = cooperateFinancierOutputPort.save(cooperateFinancier);
            log.info("coperafe rinancier == {}",savedCooperateFinancier);
            cooperateFinancierID = savedCooperateFinancier.getId();
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(savedCooperateFinancier);
        assertEquals(savedCooperateFinancier.getActivationStatus(),cooperateFinancier.getActivationStatus());
    }


    @Order(2)
    @Test
    void findCooperateFinancierByUserId() {
        CooperateFinancier foundCooperateFinancier = null;
        try{
            foundCooperateFinancier = cooperateFinancierOutputPort.findCooperateFinancierByUserId(userIdentity.getId());
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(foundCooperateFinancier);
        assertEquals(foundCooperateFinancier.getId(),cooperateFinancierID);
    }

    @Order(3)
    @Test
    void findCooperateFinancierSuperAdminByCooperateName() {
        CooperateFinancier foundCooperateFinancier = null;
        try{
            foundCooperateFinancier = cooperateFinancierOutputPort.findCooperateFinancierSuperAdminByCooperateName("NepoBABY");
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(foundCooperateFinancier);
        assertEquals(foundCooperateFinancier.getId(),cooperateFinancierID);
    }

    @AfterAll
    void tearDown() throws MeedlException {
        cooperateFinancierOutputPort.delete(cooperateFinancierID);
        cooperationOutputPort.deleteById(cooperate.getId());
        financierOutputPort.delete(financier.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
    }


}
