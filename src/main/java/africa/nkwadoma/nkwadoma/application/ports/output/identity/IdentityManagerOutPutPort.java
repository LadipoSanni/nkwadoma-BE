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

public interface IdentityManagerOutPutPort {
    UserIdentity createUser(UserIdentity userIdentity) throws MeedlException;

    void deleteUser(UserIdentity userIdentity) throws MeedlException;

    Optional<UserIdentity> getUserByEmail(String email) throws MeedlException;

    OrganizationIdentity createOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    void disableOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    ClientRepresentation getClientRepresentationByClientId(String id) throws MeedlException;

    UserIdentity createPassword(String email, String password) throws MeedlException;
    void logout(UserIdentity userIdentity) throws MeedlException;
    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;
    void changePassword(UserIdentity userIdentity)throws MeedlException;
    UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException;
    UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException;


    UserRepresentation getUserRepresentation(UserIdentity userIdentity, Boolean exactMatch) throws MeedlException;

    List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity);

    UserResource getUserResource(UserIdentity userIdentity) throws MeedlException;
    RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MeedlException;

    UserIdentity verifyUserExists(UserIdentity userIdentity) throws MeedlException;
}
