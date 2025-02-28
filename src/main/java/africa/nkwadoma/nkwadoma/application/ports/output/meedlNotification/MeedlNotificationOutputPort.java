package africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;

public interface MeedlNotificationOutputPort {
    MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException;
}
