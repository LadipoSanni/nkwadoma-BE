package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierPoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.PoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierPoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class FinancierPoliticallyExposedPersonTest {
    @Autowired
    private FinancierPoliticallyExposedPersonOutputPort financierPoliticallyExposedPersonOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private PoliticallyExposedPersonOutputPort politicallyExposedPersonOutputPort;
    private FinancierPoliticallyExposedPerson financierBeneficialOwner;
    private String beneficialOwnerId;
    private final String email = String.format("testfinancier%spoliticallyexposedpersonemail@email.com", name);;
    @BeforeAll
    void setUp() {
        financierBeneficialOwner = TestData.buildFinancierPoliticallyExposedPerson(email);
        try {
            UserIdentity userIdentity = userIdentityOutputPort.save(financierBeneficialOwner.getFinancier().getUserIdentity());
            financierBeneficialOwner.getFinancier().setUserIdentity(userIdentity);
            Financier financier = financierOutputPort.save(financierBeneficialOwner.getFinancier());
            BeneficialOwner beneficialOwner = politicallyExposedPersonOutputPort.save(financierBeneficialOwner.getBeneficialOwner());
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
    void save() {
        FinancierBeneficialOwner savedFinancierBeneficialOwner = null;
        try {
            savedFinancierBeneficialOwner = financierPoliticallyExposedPersonOutputPort.save(financierBeneficialOwner);
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
    void saveWithNull(){
        assertThrows(MeedlException.class, () -> financierPoliticallyExposedPersonOutputPort.save(null));
    }

    @Test
    @Order(2)
    void findById() {
        FinancierBeneficialOwner foundFinancierBeneficialOwner = null;
        try {
            foundFinancierBeneficialOwner = financierPoliticallyExposedPersonOutputPort.findById(beneficialOwnerId);
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
    void deleteById() {
        try {
            financierPoliticallyExposedPersonOutputPort.deleteById(beneficialOwnerId);
            politicallyExposedPersonOutputPort.deleteById(financierBeneficialOwner.getBeneficialOwner().getId());
            financierOutputPort.delete(financierBeneficialOwner.getFinancier().getId());
            userIdentityOutputPort.deleteUserByEmail(email);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> financierPoliticallyExposedPersonOutputPort.findById(beneficialOwnerId));
    }
}
