package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface IdentityManagerOutPutPort {
    UserIdentity createUser(UserIdentity userIdentity) throws MiddlException;

    UserRepresentation getUserRepresentation(UserIdentity userIdentity, boolean exactMatch) throws MiddlException;

    List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity);

    UserResource getUserResource(UserIdentity userIdentity) throws MiddlException;
    RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MiddlException;

    void deleteUser(UserIdentity userIdentity) throws MiddlException;

    Optional<UserIdentity> getUserByEmail(String email) throws MiddlException;

    OrganizationIdentity createOrganization(OrganizationIdentity organizationIdentity) throws MiddlException;
    void createPassword(String email, String password) throws MiddlException;
    AccessTokenResponse login(UserIdentity userIdentity) throws MiddlException;
    void changePassword(UserIdentity userIdentity)throws MiddlException;
    UserIdentity enableUserAccount(UserIdentity userIdentity) throws MiddlException;
    UserIdentity disableUserAccount(UserIdentity userIdentity) throws MiddlException;


}
