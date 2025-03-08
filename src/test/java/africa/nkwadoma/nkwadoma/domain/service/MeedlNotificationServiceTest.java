package africa.nkwadoma.nkwadoma.domain.service;


import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.service.email.NotificationService;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MeedlNotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    private MeedlNotification meedlNotification;
    private UserIdentity userIdentity;
    private String notificationId = UUID.randomUUID().toString();



    @BeforeEach
    void setUp() {
        userIdentity = TestData.createTestUserIdentity("nedo@gmail.com");
        meedlNotification = TestData.createNotification(userIdentity);
    }


    @Test
    void cannotSendNotificationWithNullContent() {
        meedlNotification.setContentId(null);
        assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"hhdfhjfb"})
    void cannotSendNotificationWithEmptyContentIdAndInvalidUuid(String contentId) {
        meedlNotification.setContentId(contentId);
        assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

      @Test
    void cannotSendNotificationWithNullTitle() {
        meedlNotification.setTitle(null);
        assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"hhdfhjfb"})
    void cannotSendNotificationWithEmptyTitle(String title) {
        meedlNotification.setContentId(title);
        assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

    @Test
    void cannotSendNotificationWithNullUserIdentity() {
        meedlNotification.setUser(null);
        assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

    @Test
    void cannotSendNotificationToUnExistingUser() throws MeedlException {
       when(userIdentityOutputPort.findById(userIdentity.getId()))
               .thenReturn(null);
       assertThrows(MeedlException.class, ()-> notificationService.sendNotification(meedlNotification));
    }

    @Test
    void sendNotification() throws MeedlException {
        when(userIdentityOutputPort.findById(userIdentity.getId()))
                .thenReturn(userIdentity);
        when(meedlNotificationOutputPort.save(meedlNotification))
                .thenReturn(meedlNotification);
        meedlNotification = notificationService.sendNotification(meedlNotification);
        assertNotNull(meedlNotification);
        assertEquals(meedlNotification.getUser().getId(), userIdentity.getId());
    }

    @Test
    void viewNotification() throws MeedlException {
        when(userIdentityOutputPort.findById(userIdentity.getId()))
                .thenReturn(userIdentity);
        when(meedlNotificationOutputPort.findNotificationById(notificationId))
                .thenReturn(meedlNotification);
        when(meedlNotificationOutputPort.save(meedlNotification))
                .thenReturn(meedlNotification);
        meedlNotification = notificationService.viewNotification(userIdentity.getId(),notificationId);
        assertNotNull(meedlNotification);
        assertEquals(meedlNotification.getUser().getId(),userIdentity.getId());
    }

    @Test
    void cannotViewNotificationWithNullUserId() throws MeedlException {
        assertThrows(MeedlException.class, ()->
                notificationService.viewNotification(null,notificationId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, " ","jhhjhjdhjd"})
    void cannotViewNotificationWithEmptyUserIdAndInvalidUuid(String id) throws MeedlException {
        assertThrows(MeedlException.class, ()->
                notificationService.viewNotification(id,notificationId));
    }

    @Test
    void cannotViewNotificationWithNullNotificationId() throws MeedlException {
        assertThrows(MeedlException.class, ()->
                notificationService.viewNotification(userIdentity.getId(),null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, " ","jhhjhjdhjd"})
    void cannotViewNotificationWithEmptyNotificationIdAndInvalidUUid(String id) throws MeedlException {
        assertThrows(MeedlException.class, ()->
                notificationService.viewNotification(userIdentity.getId(),id));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY," ","jjdjkjdjd"})
    void cannotViewAllNotificationWithEmptyUserIdAndInvalidUuid(String id) throws MeedlException {
        assertThrows(MeedlException.class, () ->
                notificationService.viewAllNotification(id));
    }

    @Test
    void cannotViewAllNotificationWithNullUserId() throws MeedlException {
        assertThrows(MeedlException.class, ()->
                notificationService.viewAllNotification(null));
    }

    @Test
    void viewAllNotification() throws MeedlException {
        when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
        when(meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userIdentity.getId()))
                .thenReturn(List.of(meedlNotification));
        List<MeedlNotification> meedlNotifications = notificationService.viewAllNotification(userIdentity.getId());
        assertNotNull(meedlNotifications);
        assertEquals(1,meedlNotifications.size());
    }

}
