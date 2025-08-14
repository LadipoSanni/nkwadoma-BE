package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {NextOfKinMapper.class, BankDetailMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserIdentityMapper {
    @Mapping(source = "identityVerified", target = "isIdentityVerified")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "nextOfKin", target = "nextOfKinEntity")
    @Mapping(source = "bankDetail", target = "bankDetailEntity")
    @Mapping(target = "email", expression = "java(userIdentity.getEmail() != null ? userIdentity.getEmail().toLowerCase() : null)")
    UserEntity toUserEntity(UserIdentity userIdentity);

    @Mapping(source = "identityVerified", target = "isIdentityVerified")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "nextOfKinEntity", target = "nextOfKin")
    @Mapping(source = "bankDetailEntity", target = "bankDetail")
    @Mapping(target = "email", expression = "java(userIdentity.getEmail() != null ? userIdentity.getEmail().toLowerCase() : null)")
    UserIdentity toUserIdentity(UserEntity userEntity);
}
