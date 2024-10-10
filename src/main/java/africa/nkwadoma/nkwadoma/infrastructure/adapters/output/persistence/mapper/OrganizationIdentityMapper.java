package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganizationIdentityMapper {
    @Mapping(source = "rcNumber", target = "registrationNumber")
    @Mapping(source = "tin", target = "taxIdentity")
    @Mapping(source = "serviceOffering", target = "serviceOfferingEntity")
    OrganizationEntity toOrganizationEntity(OrganizationIdentity organizationIdentity);

    @InheritInverseConfiguration
    OrganizationIdentity toOrganizationIdentity(OrganizationEntity organizationEntity);
}
