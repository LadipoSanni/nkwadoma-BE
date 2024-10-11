package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator.validateOrganizationIdentity;


@RequiredArgsConstructor
@Slf4j
public class KeycloakAdapter implements IdentityManagerOutPutPort {
    private final Keycloak keycloak;
    private final KeyCloakMapper mapper;
    @Value("${realm}")
    private String KEYCLOAK_REALM;
    @Value("${keycloak.client.id}")
    private String CLIENT_ID;

    @Value("${keycloak.server.url}")
    private String SERVER_URL;

    @Value("${keycloak.client.secret}")
    private String CLIENT_SECRET;



    @Override
    public UserIdentity createUser(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentityDetails(userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            Response response = users.create(userRepresentation);
            if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                throw new IdentityException(USER_IDENTITY_ALREADY_EXISTS.getMessage());
            }
            UserRepresentation createdUserRepresentation = getUserRepresentation(userIdentity, Boolean.TRUE);
            userIdentity.setId(createdUserRepresentation.getId());

            assignRole(userIdentity);
            //userIdentity = userIdentityOutputPort.save(userIdentity);
        } catch (NotFoundException exception) {
            throw new IdentityException(exception.getMessage());
        }
        return userIdentity;
    }
    @Override
    public void deleteUser(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getId())) {
            log.error("User id is empty");
            throw new MeedlException("User does not exist");
        }
        UserResource userResource = getUserResource(userIdentity);
        try{
            userResource.remove();
        }catch (NotFoundException exception) {
            log.info("deleteUser called with invalid user id: {}", userIdentity.getId());
            throw new MeedlException("User does not exist");
        }

    }

    @Override
    public Optional<UserIdentity> getUserByEmail(String email) throws MeedlException {
        UserRepresentation userRepresentation = findUserByEmail(email);
        if (userRepresentation == null) {
            return Optional.empty();
        }
        UserIdentity userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);
        return Optional.of(userIdentity);
    }

    @Override
    public OrganizationIdentity createOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        validateOrganizationIdentity(organizationIdentity);
        ClientRepresentation clientRepresentation = createClientRepresentation(organizationIdentity);
        Response response = keycloak.realm(KEYCLOAK_REALM).clients().create(clientRepresentation);
        if (response.getStatusInfo().equals(Response.Status.CREATED)) {
            clientRepresentation = getClientRepresentation(organizationIdentity);
            organizationIdentity.setId(clientRepresentation.getId());
        }else if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
            throw new MeedlException(CLIENT_EXIST.getMessage());
        }
        return organizationIdentity;
    }

    @Override
    public void createPassword(String email, String password) throws MeedlException {

        List<UserRepresentation> users = getUserRepresentations(email);
        if (users.isEmpty()) throw new MeedlException(USER_NOT_FOUND.getMessage());
        UserRepresentation user = users.get(0);
        UserResource userResource = keycloak.realm(KEYCLOAK_REALM).users().get(user.getId());
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        userResource.resetPassword(credential);
        user.setEmailVerified(true);
        user.setEnabled(true);
        userResource.update(user);
    }

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity) throws IdentityException {
        try {
            Keycloak keycloakClient = getKeycloak(userIdentity);
            TokenManager tokenManager = keycloakClient.tokenManager();

            return tokenManager.getAccessToken();
        } catch (NotAuthorizedException exception) {
            throw new IdentityException(exception.getMessage());
        }
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userIdentity.getNewPassword());
        List<UserRepresentation> userRepresentations = getUserRepresentations(userIdentity);
        for (UserRepresentation userRepresentation : userRepresentations){
        userRepresentation.setCredentials(List.of(credential));
        UserResource userResource = getUserResourceByKeycloakId(userIdentity.getId());
        userResource.update(userRepresentation);}
    }

    @Override
    public UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException {
        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        if (foundUser.isEnabled()) {
            throw new IdentityException(ACCOUNT_ALREADY_ENABLED.getMessage());
        }

        List<UserRepresentation> userRepresentations = getUserRepresentations(foundUser);
        for (UserRepresentation userRepresentation : userRepresentations){
            userRepresentation.setEnabled(true);
            UserResource userResource = getUserResourceByKeycloakId(userRepresentation.getId());
            userResource.update(userRepresentation);}
        return foundUser;

    }

    @Override
    public UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException {
        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        if (foundUser.isEnabled()) {
            throw new IdentityException(ACCOUNT_ALREADY_DISABLED.getMessage());
        }

        List<UserRepresentation> userRepresentations = getUserRepresentations(foundUser);
        for (UserRepresentation userRepresentation : userRepresentations){
            userRepresentation.setEnabled(false);
            UserResource userResource = getUserResourceByKeycloakId(userRepresentation.getId());
            userResource.update(userRepresentation);}
        return foundUser;

    }


    public UserResource getUserResourceByKeycloakId(String keycloakId) throws IdentityException {
        try {
            return keycloak.realm(KEYCLOAK_REALM).users().get(keycloakId);
        } catch (Exception e) {
            throw new IdentityException(ERROR_FETCHING_USER_INFORMATION.getMessage());
        }
    }

    private Keycloak getKeycloak(UserIdentity userIdentity) {
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.PASSWORD)
                .realm(KEYCLOAK_REALM)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username(userIdentity.getEmail())
                .password(userIdentity.getPassword())
                .serverUrl(SERVER_URL)
                .build();
    }


    public List<UserRepresentation> getUserRepresentations(String email) {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(email);
    }

    public ClientRepresentation getClientRepresentation(OrganizationIdentity organizationIdentity) throws MeedlException {
        return keycloak.realm(KEYCLOAK_REALM)
                .clients()
                .findByClientId(organizationIdentity.getId())
                .stream().findFirst().orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND.getMessage()));
    }

    private ClientRepresentation createClientRepresentation(OrganizationIdentity organizationIdentity) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(organizationIdentity.getName());
        clientRepresentation.setName(organizationIdentity.getName());
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setPublicClient(true);
        return clientRepresentation;
    }


    public UserRepresentation findUserByEmail(String email) throws MeedlException {
        UserIdentityValidator.validateEmail(email);
        List<UserRepresentation> foundUsers = keycloak.realm(KEYCLOAK_REALM).users().search(email);
        return foundUsers.isEmpty() ? null : foundUsers.get(0);
    }

    private void assignRole(UserIdentity userIdentity) throws MeedlException {
        try {
            RoleRepresentation roleRepresentation = getRoleRepresentation(userIdentity);
            UserResource userResource = getUserResource(userIdentity);
            userResource.roles().realmLevel().add(List.of(roleRepresentation));
        } catch (NotFoundException | IdentityException exception) {
            throw new IdentityException(String.format("Resource not found: %s", exception.getMessage()));
        }
    }

    public List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity) {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail());
    }
    public UserRepresentation getUserRepresentation(UserIdentity userIdentity, boolean exactMatch) throws MeedlException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail(),exactMatch)
                .stream().findFirst().orElseThrow(()-> new IdentityException(USER_NOT_FOUND.getMessage()));
    }
    public UserResource getUserResource(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .get(userIdentity.getId());
    }
    public RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MeedlException {
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloak
                    .realm(KEYCLOAK_REALM)
                    .roles()
                    .get(userIdentity.getRole().toUpperCase().trim())
                    .toRepresentation();
        }catch (NotFoundException exception){
            throw new IdentityException("Not Found: Role with name "+ userIdentity.getRole());
        }
        return roleRepresentation;
    }
    private void validateUserIdentity(UserIdentity userIdentity) throws MeedlException {
        log.info("Validating userIdentity {}",userIdentity);
        if (userIdentity == null)
            throw new IdentityException(INVALID_REGISTRATION_DETAILS.getMessage());
    }
    private void validateUserIdentityDetails(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getEmail())
                || StringUtils.isEmpty(userIdentity.getFirstName())
                || StringUtils.isEmpty(userIdentity.getLastName())
                || StringUtils.isEmpty(userIdentity.getRole()))
            throw new IdentityException(INVALID_REGISTRATION_DETAILS.getMessage());
        getRoleRepresentation(userIdentity);
    }
    private void validateUserIdentityDeleteDetails(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
    }

}
