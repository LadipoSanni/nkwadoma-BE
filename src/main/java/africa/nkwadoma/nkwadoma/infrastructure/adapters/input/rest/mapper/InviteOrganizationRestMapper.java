package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.InviteOrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.OrganizationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InviteOrganizationRestMapper {
    OrganizationIdentity toOrganizationIdentity(InviteOrganizationRequest inviteOrganizationRequest);
    InviteOrganizationResponse toInviteOrganizationresponse(OrganizationIdentity organizationIdentity);
    OrganizationResponse toOrganizationResponse(OrganizationIdentity organizationIdentity);
}
