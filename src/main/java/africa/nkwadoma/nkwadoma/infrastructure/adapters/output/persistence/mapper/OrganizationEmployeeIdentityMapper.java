package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEmployeeEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationEmployeeIdentityMapper {

    OrganizationEmployeeEntity toEmploymentEntity(OrganizationEmployeeIdentity organizationEmployeeIdentity);


//    @Mapping(target = "organization",ignore = true)
    @InheritInverseConfiguration
    OrganizationEmployeeIdentity toEmploymentAdminEntity(OrganizationEmployeeEntity organizationEmployeeEntity);
}
