package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = UserIdentityMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NextOfKinMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    @Mapping(target = "userEntity", source = "userIdentity")
    NextOfKinEntity toNextOfKinEntity(NextOfKin nextOfKin);

    @InheritInverseConfiguration
    NextOfKin toNextOfKin(NextOfKinEntity savedNextOfKinEntity);
}
