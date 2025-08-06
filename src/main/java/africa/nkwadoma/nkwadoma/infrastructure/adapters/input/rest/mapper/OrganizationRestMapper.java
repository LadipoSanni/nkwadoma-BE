package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.InviteColleagueRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationUpdateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.OrganizationResponse;
import jakarta.validation.Valid;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = DurationTypeMapper.class)
public interface OrganizationRestMapper {
    OrganizationIdentity toOrganizationIdentity(OrganizationRequest inviteOrganizationRequest);
    InviteOrganizationResponse toInviteOrganizationresponse(OrganizationIdentity organizationIdentity);

    @Mapping(target = "totalDebtRepaid", source = "totalDebtRepaid", defaultValue = "0")
    @Mapping(target = "totalCurrentDebt", source = "totalCurrentDebt", defaultValue = "0")
    @Mapping(target = "totalHistoricalDebt", source = "totalHistoricalDebt", defaultValue = "0")
    @Mapping(target = "repaymentRate", source = "repaymentRate", defaultValue = "0.0")
    @Mapping(target = "loanRequestCount", source = "loanRequestCount")
    OrganizationResponse toOrganizationResponse(OrganizationIdentity organizationIdentity);

    OrganizationIdentity maptoOrganizationIdentity(@Valid OrganizationUpdateRequest organizationUpdateRequest);

    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.firstName", source = "firstName")
    @Mapping(target = "userIdentity.lastName", source = "lastName")
    @Mapping(target = "userIdentity.role", source = "role")
    OrganizationIdentity mapInviteColleagueRequestToOrganizationIdentity(InviteColleagueRequest inviteColleagueRequest);
}
