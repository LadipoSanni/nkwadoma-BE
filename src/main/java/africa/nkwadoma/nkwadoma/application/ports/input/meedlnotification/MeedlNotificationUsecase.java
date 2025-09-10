package africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MeedlNotificationUsecase {
    MeedlNotification sendNotification(MeedlNotification meedlNotification) throws MeedlException;

    MeedlNotification viewNotification(String id, String notificationId) throws MeedlException;

    Page<MeedlNotification> viewAllNotification(String userId, int pageSize, int pageNumber) throws MeedlException;

    void deleteMultipleNotification(String userId, List<String> notificationIdList) throws MeedlException;
    MeedlNotification fetchNotificationCount(String id) throws MeedlException;

    Page<MeedlNotification> searchNotification(String userId, String title, int pageSize, int pageNumber) throws MeedlException;
}
