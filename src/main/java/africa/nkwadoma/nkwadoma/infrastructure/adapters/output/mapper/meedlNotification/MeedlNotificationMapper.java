package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlNotificationMapper {

    MeedlNotificationEntity toMeedlNotification(MeedlNotification meedlNotification);

    MeedlNotification toMeedlNotificationEntity(MeedlNotificationEntity meedlNotificationEntity);

    List<MeedlNotification> toMeedlNotifications(List<MeedlNotificationEntity> allNotification);
}
