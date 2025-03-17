package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.notification;


import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
    private String firstNotificationId;
    private String secondNotificationId;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    int pageSize = 10 ;
    int pageNumber = 0 ;

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
    @ValueSource(strings = {StringUtils.EMPTY," "})
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
    void cannotSaveNotificationWithNullSenderMail() {
        meedlNotification.setSenderMail(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY," "})
    void cannotSaveNotificationWithEmptySenderMail(String senderMail) {
        meedlNotification.setSenderMail(senderMail);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @Test
    void cannotSaveNotificationWithNullSenderName() {
        meedlNotification.setSenderFullName(null);
        assertThrows(MeedlException.class, () -> meedlNotificationOutputPort.save(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY," "})
    void cannotSaveNotificationWithEmptySenderName(String senderName) {
        meedlNotification.setSenderFullName(senderName);
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
           assertThrows(MeedlException.class,() ->
                   meedlNotificationOutputPort.findAllNotificationBelongingToAUser(null,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jjdhhdu"})
    void findAllNotificationBelongingToAUserWithEmptyAndInvalidId(String userId){
        assertThrows(MeedlException.class,() ->
                meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userId,pageSize,pageNumber));
    }

    @Test
    @Order(3)
    void findAllNotificationBelongingToAUserWithValidUserId(){
        Page<MeedlNotification> foundNotifications = Page.empty();
        try {
            foundNotifications = meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userId,pageSize,pageNumber);
        }catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
        assertEquals(userId, foundNotifications.getContent().get(0).getUser().getId());
        assertEquals(meedlNotificationId, foundNotifications.getContent().get(0).getId());
        assertEquals(1, foundNotifications.getContent().size());
    }

    @Test
    void findAllNotificationForAUserThatDoesNotHaveAnyNotification(){
        Page<MeedlNotification> allNotification = Page.empty();
        try {
            allNotification = meedlNotificationOutputPort.findAllNotificationBelongingToAUser(
                    "550e8400-e29b-41d4-a716-446655440000", pageSize, pageNumber);
        }catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
        assertTrue(allNotification.getContent().isEmpty());
        assertEquals(0, allNotification.getContent().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jjdhhdu"})
    void getCountOfUnreadNotificationWithEmptyAndInvalidId(String userId){
        assertThrows(MeedlException.class,() -> meedlNotificationOutputPort.getNotificationCounts(userId));
    }

    @Test
    void getCountOfUnreadNotificationNullId(){
        assertThrows(MeedlException.class,() -> meedlNotificationOutputPort.getNotificationCounts(null));
    }

    @Test
    @Order(4)
    void numberOfUnreadNotifications(){
        int numberOfUnreadNotifications = 0;
        try{
            numberOfUnreadNotifications = meedlNotificationOutputPort.getNotificationCounts(userId).getUnreadCount();
        }catch (MeedlException meedlException){
            log.info(meedlException.getMessage());
        }
        assertEquals(1, numberOfUnreadNotifications);
    }

    @Test
    @Order(5)
    void numberOfAllNotifications(){
        int numberOfAllNotifications = 0;
        try{
            numberOfAllNotifications = meedlNotificationOutputPort.getNotificationCounts(userId).getAllNotificationsCount();
        }catch (MeedlException meedlException){
            log.info(meedlException.getMessage());
        }
        assertEquals(1, numberOfAllNotifications);
    }

    @Test
    void deleteMultipleNotificationWithNull() {
        assertThrows(MeedlException.class,()->meedlNotificationOutputPort.deleteMultipleNotification(null));
    }

    @Test
    void deleteMultipleNotificationWithEmpty() {
        List<String> emptyList = Collections.emptyList();
        assertThrows(MeedlException.class, ()->meedlNotificationOutputPort.deleteMultipleNotification(emptyList));
    }


    @Test
    void deleteMultipleNotification(){

        try{
            MeedlNotification firstNotification = meedlNotificationOutputPort.save(meedlNotification);
            MeedlNotification secondNotification = meedlNotificationOutputPort.save(meedlNotification);
            firstNotificationId = firstNotification.getId();
            secondNotificationId = secondNotification.getId();
            List<String> deleteNotificationList = Arrays.asList(firstNotificationId, secondNotificationId);
            MeedlNotification foundNotification = meedlNotificationOutputPort.findNotificationById(firstNotificationId);
            assertNotNull(foundNotification);
            assertNotNull(secondNotification);
            meedlNotificationOutputPort.deleteMultipleNotification(deleteNotificationList);
        } catch (MeedlException meedlException) {
            log.info(meedlException.getMessage());
        }
        assertThrows(MeedlException.class, ()->meedlNotificationOutputPort.findNotificationById(firstNotificationId));
        assertThrows(MeedlException.class, ()->meedlNotificationOutputPort.findNotificationById(secondNotificationId));
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        meedlNotificationOutputPort.deleteNotification(meedlNotificationId);
        userIdentityOutputPort.deleteUserById(userId);
    }
}
