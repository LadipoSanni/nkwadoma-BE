package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = UserIdentityMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NextOfKinMapper {
//    @Mapping(target = "userId", source = "userId")
     NextOfKinEntity toNextOfKinEntity(NextOfKin nextOfKin);

    @InheritInverseConfiguration
    NextOfKin toNextOfKin(NextOfKinEntity savedNextOfKinEntity);
}
