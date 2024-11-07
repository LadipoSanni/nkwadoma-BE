package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring", uses = ServiceOfferingMapper.class)
public interface OrganizationIdentityMapper {
    @Mapping(source = "rcNumber", target = "registrationNumber")
    @Mapping(source = "tin", target = "taxIdentity")
    @Mapping(source = "numberOfPrograms", target = "numberOfPrograms", defaultValue = "0")
    OrganizationEntity toOrganizationEntity(OrganizationIdentity organizationIdentity);

    @InheritInverseConfiguration
    OrganizationIdentity toOrganizationIdentity(OrganizationEntity organizationEntity);

    List<ServiceOfferingEntity> toServiceOfferingEntity(List<ServiceOffering> serviceOfferings);

    @Mapping(source = "serviceOfferingEntity", target = ".")
    ServiceOffering toServiceOfferingModel(OrganizationServiceOfferingEntity organizationServiceOfferingEntity);
    List<ServiceOffering> toServiceOfferingEntitiesServiceOfferings(List<ServiceOfferingEntity> serviceOfferingEntities);

    List<ServiceOffering> toServiceOfferings(List<OrganizationServiceOfferingEntity> organizationServiceOfferings);

    @Mapping(target = "serviceOffering", source = "serviceOfferingEntity")
    OrganizationServiceOffering toOrganizationServiceOffering(OrganizationServiceOfferingEntity organizationServiceOfferingEntity);

    List<OrganizationServiceOffering> toOrganizationServiceOfferings(List<OrganizationServiceOfferingEntity> organizationServiceOfferings);

    OrganizationIdentity updateOrganizationIdentity(@MappingTarget OrganizationIdentity organizationIdentityMapTo, OrganizationIdentity organizationIdentityMapFrom);
}
