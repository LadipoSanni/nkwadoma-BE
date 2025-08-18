package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IdentityMapper {
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    UserIdentity toIdentity(UserIdentityRequest userIdentityRequest);

    UserIdentity toUserIdentity(PasswordChangeRequest passwordChangeRequest);

    @Mapping(source = "token", target = "email")
    @Mapping(source = "password", target = "password")
    UserIdentity toPasswordCreateRequest(PasswordCreateRequest passwordCreateRequest);

    UserIdentity toLoginUserIdentity(LoginRequest loginRequest);

    UserIdentityResponse toUserIdentityResponse(UserIdentity userIdentityFound);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "MFAPhoneNumber", source = "mfaRequest.mfaPhoneNumber")
    UserIdentity map(String userId, MFARequest mfaRequest);

}
