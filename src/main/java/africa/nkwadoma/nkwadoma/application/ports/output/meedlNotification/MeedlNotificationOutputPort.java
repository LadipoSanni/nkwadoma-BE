package africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;

import java.util.List;

public interface MeedlNotificationOutputPort {
    MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException;

    void deleteNotification(String meedlNotificationId) throws MeedlException;

    MeedlNotification findNotificationById(String  id) throws MeedlException;

    List<MeedlNotification> findAllNotificationBelongingToAUser(String id) throws MeedlException;

    int getNumberOfUnReadNotification(String userId) throws MeedlException;


    void deleteNotificationByUserId(String id) throws MeedlException;

    void deleteMultipleNotification(List<String> deleteNotificationList) throws MeedlException;
}
