package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.notification;


import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MeedlNotificationAdapterTest {

    private MeedlNotification meedlNotification;
    private UserIdentity userIdentity;
    private String userId;
    private String meedlNotificationId;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private MeedlNotificationOutputPort meedlNotificationOutputPort;

    @BeforeAll
    void setUp() throws MeedlException {
        userIdentity = TestData.createTestUserIdentity("lawal5@gmail.com");
        userIdentity = userIdentityOutputPort.save(userIdentity);
        userId = userIdentity.getId();
        meedlNotification = TestData.createNotification(userIdentity);
    }

    @Test
    void cannotSaveNotificationWithNullNotificationObject() {
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(null));
    }

    @Test
    void cannotSaveNotificationWithNullContentId(){
        meedlNotification.setContentId(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jjdhhdu"})
    void cannotSaveNotificationWithEmptyContentIdAndInvalidUUid(String contentId){
        meedlNotification.setContentId(contentId);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @Test
    void cannotSaveNotificationWithNullTimestamp(){
        meedlNotification.setTimestamp(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @Test
    void cannotSaveNotificationWithNullUserIdentity() {
        meedlNotification.setUser(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,""})
    void cannotSaveNotificationWithEmptyTitle(String subject) {
        meedlNotification.setTitle(subject);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @Test
    void cannotSaveNotificationWithNullTitle() {
        meedlNotification.setTitle(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @Test
    @Order(1)
    void saveNotification() {
        MeedlNotification saveNotification = null;
        try {
            saveNotification = meedlNotificationOutputPort.save(meedlNotification);
            meedlNotificationId = saveNotification.getId();
        }catch (MeedlException exception) {
            assertNotNull(saveNotification);
            assertEquals(userId, saveNotification.getUser().getId());
        }
    }

    @Test
    void findNotificationWithNullNotificationId() {
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.findNotificationById(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jjdhhdu"})
    void findNotificationWithEmptyAndInvalidNotificationId() {
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.findNotificationById(null));
    }

    @Test
    @Order(2)
    void findNotificationWithValidNotificationId() {
        MeedlNotification foundNotification = null;
            try {
                foundNotification = meedlNotificationOutputPort.findNotificationById(meedlNotificationId);
            }catch (MeedlException exception) {
                log.info(exception.getMessage());
            }
            assertNotNull(foundNotification);
            assertEquals(meedlNotificationId, foundNotification.getId());
    }

    @Test
    void findAllNotificationBelongingToAUserWithNullId(){
           assertThrows(MeedlException.class,() -> meedlNotificationOutputPort.findAllNotificationBelongingToAUser(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jjdhhdu"})
    void findAllNotificationBelongingToAUserWithEmptyAndInvalidId(String userId){
        assertThrows(MeedlException.class,() -> meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userId));
    }

    @Test
    @Order(3)
    void findAllNotificationBelongingToAUserWithValidUserId(){
        List<MeedlNotification> foundNotifications = new ArrayList<>();
        try {
            foundNotifications = meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userId);
        }catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
        assertEquals(userId, foundNotifications.get(0).getUser().getId());
        assertEquals(meedlNotificationId, foundNotifications.get(0).getId());
        assertEquals(1, foundNotifications.size());
    }

    @Test
    void findAllNotificationForAUserThatDoesNotHaveAnyNotification(){
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.findAllNotificationBelongingToAUser(
                "550e8400-e29b-41d4-a716-446655440000"));
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        meedlNotificationOutputPort.deleteNotification(meedlNotificationId);
        userIdentityOutputPort.deleteUserById(userId);
    }
}
