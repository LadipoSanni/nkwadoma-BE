package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NextOfKinAdapterTest {
    @Autowired
    private NextOfKinOutputPort nextOfKinOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private String userId;

    @BeforeAll
    void init() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).gender("Male").nationality("Nigerian").
                stateOfOrigin("Osun").dateOfBirth("29th April 1990").maritalStatus("Single").stateOfResidence("Lagos").
                residentialAddress("1, Spencer Street, Yaba, Lagos").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        try {
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(userIdentity);
            userId = savedUserIdentity.getId();
            log.info("UserIdentity saved with id {}", userId);
            nextOfKin = TestData.createNextOfKinData(savedUserIdentity);

        } catch (MeedlException e) {
            log.error("Error saving Loanee details========> {}", e.getMessage());
        }
    }


    @Test
    @Order(1)
    void saveNextOfKin() {
        NextOfKin savedNextOfKin = null;
        try {
            log.info("Saving next of kin for user with id: {}", userId);
            nextOfKin.setUserId(userId);
            savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
            userIdentity.setNextOfKin(savedNextOfKin);
            userIdentity.setId(userId);
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(userIdentity);
            log.info("Saved next of Kin: {}", savedNextOfKin);
        } catch (MeedlException e) {
            log.error("Exception saving next of kin details", e);
        }
        log.info("Saved next of kin in test before assertion {}", savedNextOfKin);
        assertNotNull(savedNextOfKin);
    }
//    @Test
//    @Order(2)
//    void findNextOfKinByUserId() {
//        Optional<NextOfKin> foundNextOfKin = null;
//        try {
//            log.info("Finding next of kin for user with id: {}", userId);
//            foundNextOfKin = nextOfKinOutputPort.findByUserId("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f");
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//        assertFalse(foundNextOfKin.isEmpty());
//    }


    @Test
    void saveNullNextOfKin() {
        assertThrows(MeedlException.class, ()-> nextOfKinOutputPort.save(null));
    }

    @AfterAll
    void tearDown() {
        try {
            NextOfKin foundNextOfKin = nextOfKinOutputPort.findByEmail(nextOfKin.getEmail());
            userIdentityOutputPort.deleteUserByEmail(userIdentity.getEmail());
            nextOfKinOutputPort.deleteNextOfKin(foundNextOfKin.getId());

        } catch (MeedlException e) {
            log.error("Error deleting details", e);
        }
    }
}