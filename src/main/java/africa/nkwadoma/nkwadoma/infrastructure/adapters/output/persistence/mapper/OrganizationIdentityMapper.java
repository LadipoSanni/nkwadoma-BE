package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationIdentityMapper {
    OrganizationEntity toOrganizationEntity(OrganizationIdentity organizationIdentity);
    OrganizationIdentity toOrganizationIdentity(OrganizationEntity organizationEntity);
}
