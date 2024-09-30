package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEmployeeEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationEmployeeIdentityMapper {

    OrganizationEmployeeEntity toOrganizationEmployeeEntity(OrganizationEmployeeIdentity organizationEmployeeIdentity);


//    @Mapping(target = "organization",ignore = true)
    @InheritInverseConfiguration
    OrganizationEmployeeIdentity toOrganizationEmployeeIdentity(OrganizationEmployeeEntity organizationEmployeeEntity);
}
