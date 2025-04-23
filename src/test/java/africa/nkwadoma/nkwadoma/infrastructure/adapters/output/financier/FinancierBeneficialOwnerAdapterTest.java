package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.BeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierBeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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
public class FinancierBeneficialOwnerAdapterTest {
    @Autowired
    private FinancierBeneficialOwnerOutputPort financierBeneficialOwnerOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private BeneficialOwnerOutputPort beneficialOwnerOutputPort;
    private FinancierBeneficialOwner financierBeneficialOwner;
    private String beneficialOwnerId;
    private final String email = "testfinancierbeneficialowneremail@email.com";
    @BeforeAll
    void setUp() {
        financierBeneficialOwner = TestData.buildFinancierBeneficialOwner(email);
        try {
            UserIdentity userIdentity = userIdentityOutputPort.save(financierBeneficialOwner.getFinancier().getUserIdentity());
            financierBeneficialOwner.getFinancier().setUserIdentity(userIdentity);
            Financier financier = financierOutputPort.save(financierBeneficialOwner.getFinancier());
            BeneficialOwner beneficialOwner = beneficialOwnerOutputPort.save(financierBeneficialOwner.getBeneficialOwner());
            financierBeneficialOwner.setBeneficialOwner(beneficialOwner);
            financierBeneficialOwner.setFinancier(financier);
            log.info("Details saved and set in test.");
        } catch (MeedlException e) {
            log.error("Failed in test financier beneficial owner adapter", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void saveBeneficialOwner() {
        FinancierBeneficialOwner savedFinancierBeneficialOwner = null;
        try {
            savedFinancierBeneficialOwner = financierBeneficialOwnerOutputPort.save(financierBeneficialOwner);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedFinancierBeneficialOwner);
        assertNotNull(savedFinancierBeneficialOwner.getId());
//        assertEquals(financierBeneficialOwner.getEntityName(), financierBeneficialOwner.getEntityName());
        log.info("Saved cooperation {}", savedFinancierBeneficialOwner);
        beneficialOwnerId = savedFinancierBeneficialOwner.getId();
    }
    @Test
    void saveBeneficialOwnerWithNull(){
        assertThrows(MeedlException.class, () -> financierBeneficialOwnerOutputPort.save(null));
    }

    @Test
    @Order(2)
    void findBeneficialOwnerById() {
        FinancierBeneficialOwner foundFinancierBeneficialOwner = null;
        try {
            foundFinancierBeneficialOwner = financierBeneficialOwnerOutputPort.findById(beneficialOwnerId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancierBeneficialOwner);
//        assertEquals(financierBeneficialOwner.getEntityName(), foundFinancierBeneficialOwner.getEntityName());
        log.info("found beneficial owner {}", foundFinancierBeneficialOwner);
    }
    @Test
    @Order(3)
    void deleteBeneficialOwnerById() {
        try {
            financierBeneficialOwnerOutputPort.deleteById(beneficialOwnerId);
            beneficialOwnerOutputPort.deleteById(financierBeneficialOwner.getBeneficialOwner().getId());
            financierOutputPort.delete(financierBeneficialOwner.getFinancier().getId());
            userIdentityOutputPort.deleteUserByEmail(email);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> financierBeneficialOwnerOutputPort.findById(beneficialOwnerId));
    }
}
