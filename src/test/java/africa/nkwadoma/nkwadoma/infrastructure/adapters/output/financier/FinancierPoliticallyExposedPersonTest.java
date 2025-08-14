package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierPoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.PoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
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
public class FinancierPoliticallyExposedPersonTest {
    @Autowired
    private FinancierPoliticallyExposedPersonOutputPort financierPoliticallyExposedPersonOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private PoliticallyExposedPersonOutputPort politicallyExposedPersonOutputPort;
    private FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson;
    private String politicallyExposedPersonId;
    private final String email = String.format("testfinancier%spoliticallyexposedpersonemail@email.com", TestUtils.generateName(4));;
    @BeforeAll
    void setUp() {
        financierPoliticallyExposedPerson = TestData.buildFinancierPoliticallyExposedPerson(email);
        try {
            UserIdentity userIdentity = userIdentityOutputPort.save(financierPoliticallyExposedPerson.getFinancier().getUserIdentity());
            financierPoliticallyExposedPerson.getFinancier().setUserIdentity(userIdentity);
            Financier financier = financierOutputPort.save(financierPoliticallyExposedPerson.getFinancier());
            PoliticallyExposedPerson politicallyExposedPerson = politicallyExposedPersonOutputPort.save(financierPoliticallyExposedPerson.getPoliticallyExposedPerson());
            financierPoliticallyExposedPerson.setPoliticallyExposedPerson(politicallyExposedPerson);
            financierPoliticallyExposedPerson.setFinancier(financier);
            log.info("Details saved and set in test ---- politically exposed person.");
        } catch (MeedlException e) {
            log.error("Failed in test financier politically exposed person adapter", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void save() {
        FinancierPoliticallyExposedPerson savedFinancierPoliticallyExposedPerson = null;
        try {
            savedFinancierPoliticallyExposedPerson = financierPoliticallyExposedPersonOutputPort.save(financierPoliticallyExposedPerson);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedFinancierPoliticallyExposedPerson);
        assertNotNull(savedFinancierPoliticallyExposedPerson.getId());
        log.info("Saved politically exposed person ...... {}", savedFinancierPoliticallyExposedPerson);
        politicallyExposedPersonId = savedFinancierPoliticallyExposedPerson.getId();
    }
    @Test
    void saveWithNull(){
        assertThrows(MeedlException.class, () -> financierPoliticallyExposedPersonOutputPort.save(null));
    }

    @Test
    @Order(2)
    void findById() {
        FinancierPoliticallyExposedPerson savedFinancierPoliticallyExposedPerson = null;
        try {
            savedFinancierPoliticallyExposedPerson = financierPoliticallyExposedPersonOutputPort.findById(politicallyExposedPersonId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedFinancierPoliticallyExposedPerson);
        assertNotNull(savedFinancierPoliticallyExposedPerson.getFinancier());
        assertNotNull(savedFinancierPoliticallyExposedPerson.getFinancier().getUserIdentity());
        assertNotNull(savedFinancierPoliticallyExposedPerson.getFinancier().getUserIdentity().getEmail());
        assertEquals(email, savedFinancierPoliticallyExposedPerson.getFinancier().getUserIdentity().getEmail().toLowerCase());
        assertNotNull(savedFinancierPoliticallyExposedPerson.getPoliticallyExposedPerson());
        assertNotNull(savedFinancierPoliticallyExposedPerson.getPoliticallyExposedPerson().getPositionHeld());
        assertNotNull(savedFinancierPoliticallyExposedPerson.getPoliticallyExposedPerson().getCountry());
        log.info("found beneficial owner {}", savedFinancierPoliticallyExposedPerson);
    }
    @Test
    @Order(3)
    void deleteById() {
        try {
            financierPoliticallyExposedPersonOutputPort.deleteById(politicallyExposedPersonId);
            politicallyExposedPersonOutputPort.deleteById(financierPoliticallyExposedPerson.getPoliticallyExposedPerson().getId());
            financierOutputPort.delete(financierPoliticallyExposedPerson.getFinancier().getId());
            userIdentityOutputPort.deleteUserByEmail(email);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> financierPoliticallyExposedPersonOutputPort.findById(politicallyExposedPersonId));
    }
}
