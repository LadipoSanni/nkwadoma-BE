package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {NextOfKinMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserIdentityMapper {
    @Mapping(source = "identityVerified", target = "isIdentityVerified")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "nextOfKin", target = "nextOfKinEntity")
    UserEntity toUserEntity(UserIdentity userIdentity);

//    @Mapping(source = "isIdentityVerified", target = "identityVerified")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "nextOfKinEntity", target = "nextOfKin")
    UserIdentity toUserIdentity(UserEntity userEntity);
}
