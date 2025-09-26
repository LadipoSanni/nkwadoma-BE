package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlnotification.PlatformRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlatformRequestMapper {
    PlatformRequestEntity map(PlatformRequest platformRequest);

    PlatformRequest map(PlatformRequestEntity platformRequestEntity);
}
