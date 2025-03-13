package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.meedlException.MeedlNotificationException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification.MeedlNotificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification.MeedlNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MeedlNotificationAdapter implements MeedlNotificationOutputPort {

    private final MeedlNotificationMapper meedlNotificationMapper;
    private final MeedlNotificationRepository meedlNotificationRepository;

    @Override
    public MeedlNotification save(MeedlNotification meedlNotification) throws MeedlException {
        MeedlValidator.validateObjectInstance(meedlNotification,"Notification object cannot be empty ");
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

    @Override
    public MeedlNotification findNotificationById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Notification id cannot be empty");
        MeedlNotificationEntity meedlNotificationEntity = meedlNotificationRepository.findById(id)
                .orElseThrow(() -> new MeedlNotificationException("Notification not found"));
        return meedlNotificationMapper.toMeedlNotificationEntity(meedlNotificationEntity);
    }

    @Override
    public List<MeedlNotification> findAllNotificationBelongingToAUser(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId,"User id cannot be empty");
        List<MeedlNotificationEntity> allNotification =
                meedlNotificationRepository.findAllByUser_Id(userId, Sort.by("timestamp").ascending());
        if (allNotification.isEmpty()) {
            throw new MeedlNotificationException("User dosen't have any notifications");
        }
        return meedlNotificationMapper.toMeedlNotifications(allNotification);
    }

    @Override
    public int getNumberOfUnReadNotification(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId,"User id cannot be empty");
        return meedlNotificationRepository.countByUserIdAndReadIsFalse(userId);
    }

    @Transactional
    @Override
    public void deleteNotificationByUserId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"User id cannot be empty");
        meedlNotificationRepository.deleteAllByUserId(id);
    }

    @Transactional
    @Override
    public void deleteMultipleNotification(List<String> deleteNotificationList) throws MeedlException {
        MeedlValidator.validateNotificationList(deleteNotificationList);
        meedlNotificationRepository.deleteAllById(deleteNotificationList);
    }
}
