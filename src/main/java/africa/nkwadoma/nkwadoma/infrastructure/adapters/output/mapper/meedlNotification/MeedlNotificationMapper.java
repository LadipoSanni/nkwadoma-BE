package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedNotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlNotificationMapper {

    MeedNotificationEntity toMeedlNotification(MeedlNotification meedlNotification);
}
