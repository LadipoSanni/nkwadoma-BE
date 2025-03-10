package africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface MeedlNotificationUsecase {
    MeedlNotification sendNotification(MeedlNotification meedlNotification) throws MeedlException;

    MeedlNotification viewNotification(String id, String notificationId) throws MeedlException;

    List<MeedlNotification> viewAllNotification(String id) throws MeedlException;
}
