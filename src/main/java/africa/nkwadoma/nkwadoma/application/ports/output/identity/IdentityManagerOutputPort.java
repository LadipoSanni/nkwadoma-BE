package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.*;

import java.util.*;

public interface IdentityManagerOutputPort {
    UserIdentity createUser(UserIdentity userIdentity) throws MeedlException;

    void deleteUser(UserIdentity userIdentity) throws MeedlException;

    Optional<UserIdentity> getUserByEmail(String email) throws MeedlException;

    OrganizationIdentity createOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    UserIdentity createPassword(String email, String password) throws MeedlException;

    void logout(UserIdentity userIdentity) throws MeedlException;

    void disableClient(OrganizationIdentity organizationIdentity) throws MeedlException;

    ClientRepresentation getClientRepresentationByClientId(String id) throws MeedlException;

    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;

    void resetPassword(UserIdentity userIdentity) throws MeedlException;

    void setPassword(UserIdentity userIdentity) throws MeedlException;

    UserIdentity verifyUserExistsAndIsEnabled(UserIdentity userIdentity) throws MeedlException;

    UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException;

    UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException;


    UserRepresentation getUserRepresentation(UserIdentity userIdentity, Boolean exactMatch) throws MeedlException;

    ClientRepresentation getClientRepresentationByName(String clientName) throws MeedlException;

    List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity);

    UserResource getUserResource(UserIdentity userIdentity) throws MeedlException;

    RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MeedlException;

    ClientResource getClientResource(String clientId);

    void deleteClient(String clientId);
}
