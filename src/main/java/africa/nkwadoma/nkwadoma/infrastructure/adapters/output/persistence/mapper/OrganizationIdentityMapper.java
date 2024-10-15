package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ServiceOfferingMapper.class)
public interface OrganizationIdentityMapper {
    @Mapping(source = "rcNumber", target = "registrationNumber")
    @Mapping(source = "tin", target = "taxIdentity")
    @Mapping(source = "serviceOffering", target = "serviceOfferingEntity")
    @Mapping(source = "numberOfPrograms", target = "numberOfPrograms", defaultValue = "0")
    OrganizationEntity toOrganizationEntity(OrganizationIdentity organizationIdentity);

    @InheritInverseConfiguration
    @Mapping(source = "serviceOfferingEntity", target = "serviceOffering")
    OrganizationIdentity toOrganizationIdentity(OrganizationEntity organizationEntity);
}
