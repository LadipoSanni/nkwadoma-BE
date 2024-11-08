package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NextOfKinMapper {
    NextOfKinEntity toNextOfKinEntity(NextOfKin nextOfKin);

    @InheritInverseConfiguration
    NextOfKin toNextOfKin(NextOfKinEntity savedNextOfKinEntity);
}
