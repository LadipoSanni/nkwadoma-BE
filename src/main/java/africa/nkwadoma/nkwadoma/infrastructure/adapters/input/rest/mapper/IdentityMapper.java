package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IdentityMapper {

    UserIdentity toIdentity(UserIdentityRequest userIdentityRequest);

}
