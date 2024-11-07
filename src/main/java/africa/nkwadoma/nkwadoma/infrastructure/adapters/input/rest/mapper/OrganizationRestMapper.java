package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.OrganizationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationRestMapper {
    OrganizationIdentity toOrganizationIdentity(OrganizationRequest inviteOrganizationRequest);
    InviteOrganizationResponse toInviteOrganizationresponse(OrganizationIdentity organizationIdentity);

    OrganizationResponse toOrganizationResponse(OrganizationIdentity organizationIdentity);
}
