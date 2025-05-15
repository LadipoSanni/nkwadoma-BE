package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.PoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.PoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class PoliticallyExposedPersonAdapterTest {
    @Autowired
    private PoliticallyExposedPersonOutputPort politicallyExposedPersonOutputPort;
    private PoliticallyExposedPerson politicallyExposedPerson;
    private String politicallyExposedPersonId;
    @BeforeAll
    void setUp() {
        politicallyExposedPerson = TestData.buildPoliticallyExposedPerson();
    }

    @Test
    @Order(1)
    void save() {
        PoliticallyExposedPerson savedPoliticallyExposedPerson = null;
        try {
            savedPoliticallyExposedPerson = politicallyExposedPersonOutputPort.save(politicallyExposedPerson);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedPoliticallyExposedPerson);
        assertNotNull(savedPoliticallyExposedPerson.getId());
        assertEquals(savedPoliticallyExposedPerson.getPositionHeld(), savedPoliticallyExposedPerson.getPositionHeld());
        log.info("Saved cooperation {}", savedPoliticallyExposedPerson);
        politicallyExposedPersonId = savedPoliticallyExposedPerson.getId();
    }
    @Test
    void saveWithNull(){
        assertThrows(MeedlException.class, () -> politicallyExposedPersonOutputPort.save(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void saveWithInvalidName(String name){
        PoliticallyExposedPerson politicallyExposedPerson = TestData.buildPoliticallyExposedPerson();
        politicallyExposedPerson.setPositionHeld(name);
        assertThrows(MeedlException.class, () -> politicallyExposedPersonOutputPort.save(politicallyExposedPerson));
    }

    @Test
    @Order(2)
    void findById() {
        PoliticallyExposedPerson foundPoliticallyExposed = null;
        try {
            foundPoliticallyExposed = politicallyExposedPersonOutputPort.findById(politicallyExposedPersonId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundPoliticallyExposed);
        assertNotNull(foundPoliticallyExposed.getCountry());
        assertEquals(politicallyExposedPerson.getPositionHeld(), foundPoliticallyExposed.getPositionHeld());
        assertEquals(politicallyExposedPerson.getCountry(), foundPoliticallyExposed.getCountry());
        log.info("found beneficial owner {}", foundPoliticallyExposed);
    }
    @Test
    @Order(3)
    void deleteById() {
        try {
            politicallyExposedPersonOutputPort.deleteById(politicallyExposedPersonId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> politicallyExposedPersonOutputPort.findById(politicallyExposedPersonId));
    }
}
