package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.ViewOrganizationAdminRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationEmployeeRestMapper {
    @Mapping(target = "fullName", expression =
            "java(concatenateNames(organizationEmployeeIdentity.getMeedlUser().getFirstName()," +
                    " organizationEmployeeIdentity.getMeedlUser().getLastName()))"
    )
    @Mapping(target = "email", source = "meedlUser.email")
    @Mapping(target = "role", source = "meedlUser.role")
    OrganizationEmployeeResponse toOrganizationEmployeeResponse(OrganizationEmployeeIdentity organizationEmployeeIdentity);

    default String concatenateNames(String firstName, String lastName) {
        return String.format("%s %s", firstName, lastName);
    }

    @Mapping(target = "meedlUser.id", source = "userId")
    @Mapping(target = "identityRoles", source = "viewOrganizationAdminRequest.identityRoles")
    @Mapping(target = "status", source = "viewOrganizationAdminRequest.status")
    @Mapping(target = "pageNumber", source = "viewOrganizationAdminRequest.pageNumber")
    @Mapping(target = "pageSize", source = "viewOrganizationAdminRequest.pageSize")
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(String userId, ViewOrganizationAdminRequest viewOrganizationAdminRequest);

}
