package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification.NotificationProjection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlNotificationMapper {

    MeedlNotificationEntity toMeedlNotificationEntity(MeedlNotification meedlNotification);

    MeedlNotification toMeedlNotification(MeedlNotificationEntity meedlNotificationEntity);

    List<MeedlNotification> toMeedlNotifications(List<MeedlNotificationEntity> allNotification);


    MeedlNotification mapProjectionToNotificaltion(NotificationProjection notificationProjection);
}
