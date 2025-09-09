package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface IdentityManagerOutputPort {
    UserIdentity createUser(UserIdentity userIdentity) throws MeedlException;
    UserIdentity updateUserData(UserIdentity userIdentity) throws MeedlException;

    void deleteUser(UserIdentity userIdentity) throws MeedlException;

    Optional<UserIdentity> getUserByEmail(String email) throws MeedlException;

    OrganizationIdentity createKeycloakClient(OrganizationIdentity organizationIdentity) throws MeedlException;
    UserIdentity createPassword(UserIdentity userIdentity) throws MeedlException;

    List<UserIdentity> getUsersByRole(String roleName) throws MeedlException;

    void changeUserRole(UserIdentity userIdentity, String newRole) throws MeedlException;

    void logout(UserIdentity userIdentity) throws MeedlException;
    void enableClient(OrganizationIdentity foundOrganization) throws MeedlException;
    void disableClient(OrganizationIdentity organizationIdentity) throws MeedlException;

    ClientRepresentation getClientRepresentationByClientId(String id) throws MeedlException;

    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;
    AccessTokenResponse refreshToken(UserIdentity userIdentity) throws MeedlException;

    void resetPassword(UserIdentity userIdentity) throws MeedlException;

    void setPassword(UserIdentity userIdentity)throws MeedlException;

    UserIdentity verifyUserExistsAndIsEnabled(UserIdentity userIdentity) throws MeedlException;

    UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException;
    UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException;

    UserRepresentation getUserRepresentation(UserIdentity userIdentity, Boolean exactMatch) throws MeedlException;

    ClientRepresentation getClientRepresentationByName(String clientName) throws MeedlException;

    List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity);

    UserResource getUserResource(UserIdentity userIdentity) throws MeedlException;
    RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MeedlException;

    UserIdentity getUserById(String userId) throws MeedlException;

    ClientResource getClientResource(String clientId);

    void deleteClient(String clientId);

    boolean userExistByEmail(String userEmail);

    boolean clientExistByName(String organizationName);
}
