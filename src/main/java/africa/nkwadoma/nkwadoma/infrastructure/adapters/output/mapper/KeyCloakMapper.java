package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface KeyCloakMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "username")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "enabled", target = "enabled")
    UserRepresentation map(UserIdentity user);

    @InheritInverseConfiguration
    UserIdentity mapUserRepresentationToUserIdentity(UserRepresentation userRepresentation);

    @InheritInverseConfiguration
    OrganizationIdentity mapUserRepresentationToUserEntity(UserRepresentation userRepresentation);



}
