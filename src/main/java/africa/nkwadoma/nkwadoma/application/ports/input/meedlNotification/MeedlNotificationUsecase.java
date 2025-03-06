package africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;

public interface MeedlNotificationUsecase {
    MeedlNotification sendNotification(MeedlNotification meedlNotification) throws MeedlException;

    MeedlNotification viewNotification(String id, String notificationId) throws MeedlException;
}
