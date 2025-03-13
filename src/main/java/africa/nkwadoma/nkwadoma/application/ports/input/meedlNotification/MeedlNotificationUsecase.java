package africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import org.springframework.data.domain.Page;

public interface MeedlNotificationUsecase {
    MeedlNotification sendNotification(MeedlNotification meedlNotification) throws MeedlException;

    MeedlNotification viewNotification(String id, String notificationId) throws MeedlException;

    Page<MeedlNotification> viewAllNotification(String id, int pageSize, int pageNumber) throws MeedlException;

    void deleteMultipleNotification(List<String> notificationIdList) throws MeedlException;
    MeedlNotification getNumberOfUnReadNotification(String id) throws MeedlException;
}
