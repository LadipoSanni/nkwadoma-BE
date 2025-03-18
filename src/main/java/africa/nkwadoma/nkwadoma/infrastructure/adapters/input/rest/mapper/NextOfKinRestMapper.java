package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {DurationTypeMapper.class, StringTrimMapper.class})
public interface NextOfKinRestMapper {
    @Mapping(target = "firstName", qualifiedByName = "trimString")
    @Mapping(target = "lastName", qualifiedByName = "trimString")
    @Mapping(target = "email", qualifiedByName = "trimString")
    @Mapping(target = "phoneNumber", qualifiedByName = "trimString")
    @Mapping(target = "nextOfKinRelationship", qualifiedByName = "trimString")
    @Mapping(target = "contactAddress", qualifiedByName = "trimString")
    @Mapping(target = "alternateContactAddress", qualifiedByName = "trimString")
    @Mapping(target = "alternatePhoneNumber", qualifiedByName = "trimString")
    @Mapping(target = "alternateEmail", qualifiedByName = "trimString")
    @Mapping(source = "userId", target = "userIdentity.id")
    NextOfKin toNextOfKin(NextOfKinRequest nextOfKinRequest, String userId);

    NextOfKinResponse toNextOfKinResponse(NextOfKin nextOfKin);
}
