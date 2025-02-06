package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityVerificationMapper {
    @Mapping(target = "email", ignore = true)
    UserIdentity updateUserIdentity(PremblyBvnResponse.BvnData premblyBvnResponse, @MappingTarget UserIdentity userIdentity);
}
