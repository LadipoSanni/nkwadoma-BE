package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationEmployeeIdentityMapper {
    OrganizationEmployeeEntity toOrganizationEmployeeEntity(OrganizationEmployeeIdentity organizationEmployeeIdentity);

    @InheritInverseConfiguration
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeEntity organizationEmployeeEntity);
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeProjection organizationEmployeeProjection);
}
