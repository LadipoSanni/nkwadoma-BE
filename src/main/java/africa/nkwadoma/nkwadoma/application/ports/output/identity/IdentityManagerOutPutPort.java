package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IdentityManagerOutPutPort {
    UserIdentity createUser(UserIdentity userIdentity) throws InfrastructureException;

    UserRepresentation getUserRepresentation(UserIdentity userIdentity, boolean exactMatch) throws InfrastructureException;

    List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity);

    UserResource getUserResource(UserIdentity userIdentity) throws InfrastructureException;
    RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws InfrastructureException;

    void deleteUser(UserIdentity userIdentity) throws InfrastructureException;
}
