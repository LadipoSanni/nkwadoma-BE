package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationEmployeeIdentityMapper {
    @Mapping(target = "activationStatus", source = "activationStatus")
    OrganizationEmployeeEntity toOrganizationEmployeeEntity(OrganizationEmployeeIdentity organizationEmployeeIdentity);

    @InheritInverseConfiguration
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeEntity organizationEmployeeEntity);

    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeEntityProjection organizationEmployeeEntity);

    @Mapping(target = "meedlUser.firstName", source = "firstName")
    @Mapping(target = "meedlUser.lastName", source = "lastName")
    @Mapping(target = "meedlUser.email", source = "email")
    @Mapping(target = "meedlUser.createdAt", source = "createdAt")
    @Mapping(target = "meedlUser.id", source = "userId")
    @Mapping(target = "meedlUser.role", source = "role")
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeProjection organizationEmployeeProjection);
}
