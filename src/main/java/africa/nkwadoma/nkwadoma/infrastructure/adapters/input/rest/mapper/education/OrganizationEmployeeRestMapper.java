package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationEmployeeRestMapper {
    @Mapping(target = "fullName", expression =
            "java(concatenateNames(organizationEmployeeIdentity.getMeedlUser().getFirstName()," +
                    " organizationEmployeeIdentity.getMeedlUser().getLastName()))"
    )
    @Mapping(target = "firstName", source = "meedlUser.firstName")
    @Mapping(target = "lastName", source = "meedlUser.lastName")
    @Mapping(target = "email", source = "meedlUser.email")
    @Mapping(target = "createdAt", source = "meedlUser.createdAt")
    @Mapping(target = "role", source = "meedlUser.role")
    @Mapping(target = "userId", source = "meedlUser.id")
    OrganizationEmployeeResponse toOrganizationEmployeeResponse(OrganizationEmployeeIdentity organizationEmployeeIdentity);

    default String concatenateNames(String firstName, String lastName) {
        return String.format("%s %s", firstName, lastName);
    }

    @Mapping(target = "meedlUser.id", source = "userId")
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(String userId,
                                                                String name,
                                                                Set<IdentityRole> identityRoles,
                                                                Set<ActivationStatus> activationStatuses,
                                                                int pageNumber,
                                                                int pageSize);

}
