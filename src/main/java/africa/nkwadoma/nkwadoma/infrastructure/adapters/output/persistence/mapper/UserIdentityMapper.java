package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserIdentityMapper {
    @Mapping(source = "identityVerified", target = "isIdentityVerified")
    @Mapping(source = "firstName", target = "firstName")
    UserEntity toUserEntity(UserIdentity userIdentity);

    @Mapping(source = "identityVerified", target = "isIdentityVerified")
    @Mapping(source = "firstName", target = "firstName")
    UserIdentity toUserIdentity(UserEntity userEntity);

    UserEntity updateUserEntity(@MappingTarget UserEntity userEntity, UserIdentity userIdentity);

    UserIdentity updateUser(UserIdentity userIdentity, @MappingTarget UserIdentity existingUserIdentity);
}
