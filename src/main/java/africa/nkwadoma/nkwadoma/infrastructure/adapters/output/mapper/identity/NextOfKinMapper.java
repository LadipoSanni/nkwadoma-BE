package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NextOfKinMapper {
//    @Mapping(target = "loaneeEntity", source = "loanee")
//    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    NextOfKinEntity toNextOfKinEntity(NextOfKin nextOfKin);

    @InheritInverseConfiguration
    NextOfKin toNextOfKin(NextOfKinEntity savedNextOfKinEntity);
}
