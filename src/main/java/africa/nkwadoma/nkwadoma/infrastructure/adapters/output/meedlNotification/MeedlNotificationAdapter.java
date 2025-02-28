package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification.MeedlNotificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedNotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MeedlNotificationAdapter implements MeedlNotificationOutputPort {


    private final MeedlNotificationMapper meedlNotificationMapper;

    @Override
    public MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException {
        meedlNotification.validate();
        MeedNotificationEntity meedNotificationEntity =
                meedlNotificationMapper.toMeedlNotification(meedlNotification);

        return null;
    }
}
