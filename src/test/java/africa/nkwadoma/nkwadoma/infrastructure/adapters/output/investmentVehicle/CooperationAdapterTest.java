package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
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
public class CooperationAdapterTest {
    @Autowired
    private CooperationOutputPort cooperationOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private Cooperation cooperation;
    private String cooperationId;
    private String userIdentityId;
    private final String email = "testemail@email.com";
    @BeforeAll
    void setUp() {
        UserIdentity userIdentity = saveTestUser(email);
        userIdentityId = userIdentity.getId();
        cooperation = TestData.buildCooperation("cooperation adapter test", userIdentity);

    }
    private UserIdentity saveTestUser(String email){
        UserIdentity userIdentity = TestData.createTestUserIdentity(email);
        try {
            return userIdentityOutputPort.save(userIdentity);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(1)
    void saveCooperation() {
        Cooperation savedCooperation = null;
        try {
            savedCooperation = cooperationOutputPort.save(cooperation);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedCooperation);
        assertNotNull(savedCooperation.getId());
        assertEquals(cooperation.getName(), savedCooperation.getName());
        assertNotNull(savedCooperation.getUserIdentity());
        assertEquals(cooperation.getUserIdentity().getEmail(), savedCooperation.getUserIdentity().getEmail());
        log.info("Saved cooperation {}", savedCooperation);
        cooperationId = savedCooperation.getId();
    }
    @Test
    @Order(2)
    void saveCooperationWithTheSameEmailNoId() {
        assertThrows(MeedlException.class, ()-> cooperationOutputPort.save(cooperation));
    }
    @Test
    void saveCooperationWithNull(){
        assertThrows(MeedlException.class, () -> cooperationOutputPort.save(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void saveCooperationWithInvalidName(String name){
        Cooperation cooperation = TestData.buildCooperation(name, TestData.createTestUserIdentity(email));
        assertThrows(MeedlException.class, () -> cooperationOutputPort.save(cooperation));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void saveCooperationWithInvalidEmail(String name){
        Cooperation cooperation = TestData.buildCooperation("test name cooperation", TestData.createTestUserIdentity(name));
        assertThrows(MeedlException.class, () -> cooperationOutputPort.save(cooperation));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void findCooperationByInvalidEmail(String email){
        assertThrows(MeedlException.class, () -> cooperationOutputPort.findByEmail(email));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void findCooperationByInvalidCompanyName(String companyName){
        assertThrows(MeedlException.class, () -> cooperationOutputPort.findByEmail(companyName));
    }
    @Test
    @Order(3)
    void findCooperationById() {
        Cooperation foundCooperation = null;
        try {
            foundCooperation = cooperationOutputPort.findById(cooperationId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundCooperation);
        assertEquals(cooperation.getName(), foundCooperation.getName());
        log.info("found cooperation {}", foundCooperation);
    }
    @Test
    @Order(4)
    void findCooperationByEmail() {
        log.info("Cooperation email to find {} in test", email);
        Cooperation foundCooperation = null;
        try {
            foundCooperation = cooperationOutputPort.findByEmail(email);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundCooperation);
        assertNotNull(foundCooperation.getUserIdentity());
        assertEquals(cooperation.getName(), foundCooperation.getName());
        assertEquals(cooperation.getUserIdentity().getEmail(), foundCooperation.getUserIdentity().getEmail());
        log.info("found cooperation {}", foundCooperation);
    }
    @Test
    @Order(5)
    void findCooperationByName() {
        Cooperation foundCooperation = null;
        try {
            foundCooperation = cooperationOutputPort.findByName(cooperation.getName());
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundCooperation);
        assertEquals(cooperation.getName(), foundCooperation.getName());
        log.info("found cooperation {}", foundCooperation);
    }
    @Test
    @Order(6)
    void deleteCooperationById() {
        try {
            cooperationOutputPort.deleteById(cooperationId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> cooperationOutputPort.findById(cooperationId));
    }
//    @AfterAll
    void tearDown() throws MeedlException {
        userIdentityOutputPort.deleteUserById(userIdentityId);
        log.info("Test data deleted after test");
    }

}
