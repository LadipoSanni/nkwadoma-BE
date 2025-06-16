package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.meedlexception.MeedlNotificationException;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification.MeedlNotificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification.MeedlNotificationRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification.NotificationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                meedlNotificationMapper.toMeedlNotificationEntity(meedlNotification);
        log.info("is read after mapping notification {}",meedlNotification.isRead());
        meedlNotificationEntity.setRead(meedlNotification.isRead());
        log.info("is read mapping manually {}",meedlNotification.isRead());
        meedlNotificationEntity = meedlNotificationRepository.save(meedlNotificationEntity);
        return meedlNotificationMapper.toMeedlNotification(meedlNotificationEntity);
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
        return meedlNotificationMapper.toMeedlNotification(meedlNotificationEntity);
    }

    @Override
    public Page<MeedlNotification> findAllNotificationBelongingToAUser(String userId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(userId,"User id cannot be empty");
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize,Sort.by("timestamp").descending());
        Page<MeedlNotificationEntity> notificationEntities =
                meedlNotificationRepository.findAllByUser_Id(pageRequest,userId);
        if (notificationEntities.isEmpty()) {
            return Page.empty();
        }
        log.info("notification {}" , notificationEntities.map(meedlNotificationMapper::toMeedlNotification).getContent());
        return notificationEntities.map(meedlNotificationMapper::toMeedlNotification);
    }

    @Override
    public MeedlNotification getNotificationCounts(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId,"User id cannot be empty");
        NotificationProjection notificationProjection =
                meedlNotificationRepository.getNotificationCounts(userId);
        return meedlNotificationMapper.mapProjectionToNotificaltion(notificationProjection);
    }

    @Transactional
    @Override
    public void deleteNotificationByUserId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"User id cannot be empty");
        meedlNotificationRepository.deleteAllByUserId(id);
    }


    @Transactional
    @Override
    public void deleteMultipleNotification(String userId, List<String> deleteNotificationList) throws MeedlException {
        MeedlValidator.validateUUID(userId, MeedlMessages.USER_ID_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateNotificationListAndFilter(deleteNotificationList);
        meedlNotificationRepository.deleteByUserIdAndNotificationIds(userId, deleteNotificationList);
    }

    @Override
    public Page<MeedlNotification> searchNotification(String userId, String title, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(userId,"User id cannot be empty");
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize,Sort.by("timestamp").descending());
        Page<MeedlNotificationEntity> notificationEntities =
                meedlNotificationRepository.searchByUserIdAndTitleContainingIgnoreCase(pageRequest,userId,title);
        if (notificationEntities.isEmpty()) {
            return Page.empty();
        }
        log.info("notification {}" , notificationEntities.map(meedlNotificationMapper::toMeedlNotification).getContent());
        return notificationEntities.map(meedlNotificationMapper::toMeedlNotification);
    }


}
