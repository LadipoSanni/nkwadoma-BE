package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.notification;


import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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




    @AfterAll
    void cleanUp() throws MeedlException {
        meedlNotificationOutputPort.deleteNotification(meedlNotificationId);
        userIdentityOutputPort.deleteUserById(userId);
    }
}
