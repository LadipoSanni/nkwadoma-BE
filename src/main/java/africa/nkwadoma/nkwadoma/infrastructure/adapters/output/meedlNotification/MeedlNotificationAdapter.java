package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification.MeedlNotificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification.MeedlNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MeedlNotificationAdapter implements MeedlNotificationOutputPort {

    private final MeedlNotificationMapper meedlNotificationMapper;
    private final MeedlNotificationRepository meedlNotificationRepository;

    @Override
    public MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException {
        meedlNotification.validate();
        MeedlNotificationEntity meedlNotificationEntity =
                meedlNotificationMapper.toMeedlNotification(meedlNotification);
        meedlNotificationEntity = meedlNotificationRepository.save(meedlNotificationEntity);
        return meedlNotificationMapper.toMeedlNotificationEntity(meedlNotificationEntity);
    }

    @Override
    public void deleteNotification(String meedlNotificationId) throws MeedlException {
        MeedlValidator.validateUUID(meedlNotificationId,"id cannot be empty");
        meedlNotificationRepository.deleteById(meedlNotificationId);
    }
}
