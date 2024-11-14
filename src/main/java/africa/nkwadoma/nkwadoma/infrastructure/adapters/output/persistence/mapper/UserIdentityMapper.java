package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserIdentityMapper {

    UserEntity toUserEntity(UserIdentity userIdentity);

    @Mapping(source = "firstName", target = "firstName")
    UserIdentity toUserIdentity(UserEntity userEntity);



}
