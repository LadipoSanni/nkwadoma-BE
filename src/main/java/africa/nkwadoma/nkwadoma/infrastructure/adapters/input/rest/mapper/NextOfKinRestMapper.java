package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = DurationTypeMapper.class)
public interface NextOfKinRestMapper {
    @Mapping(source = "alternateEmail", target = "loanee.userIdentity.alternateEmail")
    @Mapping(source = "alternatePhoneNumber", target = "loanee.userIdentity.alternatePhoneNumber")
    @Mapping(source = "alternateContactAddress", target = "loanee.userIdentity.alternateContactAddress")
    NextOfKin toNextOfKin(NextOfKinRequest nextOfKinRequest);

    NextOfKinResponse toNextOfKinResponse(NextOfKin nextOfKin);
}
