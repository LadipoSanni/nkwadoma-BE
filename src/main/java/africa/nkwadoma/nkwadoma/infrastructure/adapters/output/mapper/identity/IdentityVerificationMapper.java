package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityVerificationMapper {
    @Mapping(target = "email", ignore = true)
//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", source = "birthDate")
    @Mapping(target = "lgaOfOrigin", source = "birthLGA")
    @Mapping(target = "nationality", source = "birthCountry")
    @Mapping(target = "stateOfOrigin", source = "birthState")
    @Mapping(target = "residentialAddress", source = "residenceAddress")
    @Mapping(target = "stateOfResidence", source = "residenceState")
    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "phoneNumber", source = "telephoneNo")
    @Mapping(target = "lastName", source = "lastname")
    UserIdentity updateUserIdentity(PremblyNinResponse.NinData premblyBvnResponse, @MappingTarget UserIdentity userIdentity);


    @Mapping(target = "email", ignore = true)
//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "lgaOfOrigin", source = "lgaOfOrigin")
    @Mapping(target = "nationality", source = "nationality")
    @Mapping(target = "stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "residentialAddress", source = "residentialAddress")
    @Mapping(target = "stateOfResidence", source = "stateOfResidence")
    UserIdentity updateUserIdentityForBvn(PremblyBvnResponse.BvnData data,@MappingTarget UserIdentity userIdentity);
}
