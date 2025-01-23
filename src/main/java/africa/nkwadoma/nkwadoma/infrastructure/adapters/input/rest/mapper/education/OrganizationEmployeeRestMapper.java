package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import org.mapstruct.*;

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
}
