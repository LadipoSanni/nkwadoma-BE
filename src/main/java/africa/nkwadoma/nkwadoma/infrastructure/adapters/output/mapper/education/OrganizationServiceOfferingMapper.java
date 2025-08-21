package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationServiceOffering;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationServiceOfferingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface OrganizationServiceOfferingMapper {
    OrganizationServiceOfferingEntity toOrganizationServiceOfferingEntity(OrganizationServiceOffering organizationServiceOffering);

    OrganizationServiceOffering toOrganizationServiceOffering(OrganizationServiceOfferingEntity organizationServiceOfferingEntity);
}
