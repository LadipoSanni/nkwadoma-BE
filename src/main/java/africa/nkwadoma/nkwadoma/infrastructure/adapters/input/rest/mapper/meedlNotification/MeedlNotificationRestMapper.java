package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationReponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlNotificationRestMapper {


    @Mapping(target = "firstName", source = "user.firstName")
    MeedlNotificationReponse toMeedlNotificationResponse(MeedlNotification meedlNotification);

    List<MeedlNotificationReponse> toMeedlNotificationResponses(List<MeedlNotification> meedlNotifications);
}
