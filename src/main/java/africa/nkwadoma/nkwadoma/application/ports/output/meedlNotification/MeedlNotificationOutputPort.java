package africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import org.springframework.data.domain.Page;

public interface MeedlNotificationOutputPort {
    MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException;

    void deleteNotification(String meedlNotificationId) throws MeedlException;

    MeedlNotification findNotificationById(String  id) throws MeedlException;

    Page<MeedlNotification> findAllNotificationBelongingToAUser(String id, int pageSize, int pageNumber) throws MeedlException;

    MeedlNotification getNotificationCounts(String userId) throws MeedlException;

    void deleteNotificationByUserId(String id) throws MeedlException;


}
