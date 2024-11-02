package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
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
        log.info("Done validating user identity details in keycloak adapter : {}",userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            Response response = users.create(userRepresentation);
            if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                log.error("{} - {} --- Error occurred on attempting to create user on keycloak", Response.Status.CONFLICT, USER_IDENTITY_ALREADY_EXISTS.getMessage());
                throw new IdentityException(USER_IDENTITY_ALREADY_EXISTS.getMessage());
            }
            UserRepresentation createdUserRepresentation = getUserRepresentation(userIdentity, Boolean.TRUE);
            userIdentity.setId(createdUserRepresentation.getId());

            assignRole(userIdentity);
            log.info("User created on keycloak, role assigned : {}", createdUserRepresentation.getId());
        } catch (NotFoundException exception) {
            log.error("{} - {} --- Error occurred on attempting to create user on keycloak", exception.getClass().getName(), exception.getMessage());
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
        log.info("Keycloak service validated organization ... {}", organizationIdentity);
        ClientRepresentation clientRepresentation = createClientRepresentation(organizationIdentity);
        Response response = keycloak.realm(KEYCLOAK_REALM).clients().create(clientRepresentation);
        if (response.getStatusInfo().equals(Response.Status.CREATED)) {
            clientRepresentation = getClientRepresentationByName(organizationIdentity.getName());
            organizationIdentity.setId(clientRepresentation.getId());
            log.info("Client created successfully. Name: {}", organizationIdentity.getName());
        }else if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
            throw new MeedlException(CLIENT_EXIST.getMessage());
        }
        return organizationIdentity;
    }

    @Override
    public UserIdentity createPassword(String email, String password) throws MeedlException {
        MeedlValidator.validateDataElement(email);
        MeedlValidator.validateDataElement(password);
        password = password.trim();
        List<UserRepresentation> users = getUserRepresentations(email);
        if (users.isEmpty()) throw new MeedlException(USER_NOT_FOUND.getMessage());
        UserRepresentation userRepresentation = users.get(0);
        log.info("User ID for user creating password : {}", userRepresentation.getId());

        UserIdentity userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);
        UserResource userResource = getUserResource(userIdentity);

        CredentialRepresentation credential = createCredentialRepresentation(password);
        userResource.resetPassword(credential);

        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.TRUE);
        userResource.update(userRepresentation);
        userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);

        return userIdentity;
    }

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateDataElement(userIdentity.getEmail());
        MeedlValidator.validateDataElement(userIdentity.getPassword());
        log.info("User login credentials: {}, {}", userIdentity.getEmail(), userIdentity.getPassword());
        try {
            Keycloak keycloakClient = getKeycloak(userIdentity);
            TokenManager tokenManager = keycloakClient.tokenManager();
            return tokenManager.getAccessToken();
        } catch (NotAuthorizedException | BadRequestException exception ) {
            log.info("Error logging in user: {}", exception.getMessage());
            throw new IdentityException(IdentityMessages.INVALID_EMAIL_OR_PASSWORD.getMessage());
        }
    }
    @Override
    public UserIdentity verifyUserExists(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        UserRepresentation userRepresentation = getUserRepresentation(userIdentity, true);
        MeedlValidator.validateUUID(userRepresentation.getId());
        userIdentity.setId(userRepresentation.getId());
        return userIdentity;
    }



    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        if (userIdentity == null) {
            throw new MeedlException("User identity is null");
        }
        UserIdentityValidator.validatePassword(userIdentity.getNewPassword());
        CredentialRepresentation credential = createCredentialRepresentation(userIdentity.getNewPassword());
        updateUserCredentialOnKeyCloak(userIdentity, credential);
    }

    private void updateUserCredentialOnKeyCloak(UserIdentity userIdentity, CredentialRepresentation credential) throws IdentityException {
        List<UserRepresentation> userRepresentations = getUserRepresentations(userIdentity);
        for (UserRepresentation userRepresentation : userRepresentations){
        userRepresentation.setCredentials(List.of(credential));
        UserResource userResource = getUserResourceByKeycloakId(userIdentity.getId());
        userResource.update(userRepresentation);}
    }

    private static CredentialRepresentation createCredentialRepresentation(String password) throws MeedlException {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }

    @Override
    public UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validateDataElement(userIdentity.getReactivationReason());
        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail().trim())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        if (foundUser.isEnabled()) {
            throw new IdentityException(ACCOUNT_ALREADY_ENABLED.getMessage());
        }

        List<UserRepresentation> userRepresentations = getUserRepresentations(foundUser);
        for (UserRepresentation userRepresentation : userRepresentations){
            userRepresentation.setEnabled(true);
            UserResource userResource = getUserResourceByKeycloakId(userRepresentation.getId());
            userResource.update(userRepresentation);}
        UserRepresentation userRepresentation = mapper.map(foundUser);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.TRUE);
        UserResource userResource = getUserResource(userIdentity);
        userResource.update(userRepresentation);
        userIdentity.setEnabled(Boolean.TRUE);
        userIdentity.setEmailVerified(Boolean.TRUE);
        return userIdentity;
    }

    @Override
    public UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        log.info("validate user email  {}", userIdentity.getEmail());
        MeedlValidator.validateDataElement(userIdentity.getEmail());
        MeedlValidator.validateDataElement(userIdentity.getDeactivationReason());

        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail().trim())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));

        if (!foundUser.isEnabled()) {
            log.warn("The status of the found user is...  {} id : {}", foundUser.isEnabled(), foundUser.getId() );
            throw new IdentityException(ACCOUNT_ALREADY_DISABLED.getMessage());
        }

        List<UserRepresentation> userRepresentations = getUserRepresentations(foundUser);
        for (UserRepresentation userRepresentation : userRepresentations){
            userRepresentation.setEnabled(Boolean.FALSE);
            UserResource userResource = getUserResourceByKeycloakId(userRepresentation.getId());
            userResource.update(userRepresentation);}
        userIdentity.setEnabled(Boolean.FALSE);
        userIdentity.setEmailVerified(Boolean.FALSE);
        log.info("after deactivation on keycloak {}", userIdentity);
        return userIdentity;

    }


    public UserResource getUserResourceByKeycloakId(String keycloakId) throws IdentityException {
        try {
            return keycloak.realm(KEYCLOAK_REALM).users().get(keycloakId);
        } catch (Exception e) {
            throw new IdentityException(ERROR_FETCHING_USER_INFORMATION.getMessage());
        }
    }

    private Keycloak getKeycloak(UserIdentity userIdentity) {
        String email = userIdentity.getEmail().trim();
        String password = userIdentity.getPassword().trim();

        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.PASSWORD)
                .realm(KEYCLOAK_REALM)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username(email)
                .password(password)
                .serverUrl(SERVER_URL)
                .build();
    }


    public List<UserRepresentation> getUserRepresentations(String email) {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(email);
    }
    @Override
    public ClientResource getClientResource(String clientId) {
        return keycloak.realm(KEYCLOAK_REALM)
                .clients()
                .get(clientId);
    }

    @Override
    public void deleteClient(String clientId) {
        ClientResource clientResource = getClientResource(clientId);
        clientResource.remove();
    }

    @Override
    public ClientRepresentation getClientRepresentationByName(String clientName) throws MeedlException {
        return keycloak.realm(KEYCLOAK_REALM)
                .clients()
                .findByClientId(clientName)
                .stream().findFirst().orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND.getMessage()));
    }

    private ClientRepresentation createClientRepresentation(OrganizationIdentity organizationIdentity) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(organizationIdentity.getName());
        clientRepresentation.setName(organizationIdentity.getName());
        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.TRUE);
        clientRepresentation.setPublicClient(Boolean.TRUE);
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
    public UserRepresentation getUserRepresentation(UserIdentity userIdentity, Boolean exactMatch) throws MeedlException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail(), exactMatch)
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
        if (userIdentity.getRole() == null || StringUtils.isEmpty(userIdentity.getRole().name()))
            throw new IdentityException(INVALID_VALID_ROLE.getMessage());
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloak
                    .realm(KEYCLOAK_REALM)
                    .roles()
                    .get(userIdentity.getRole().name().toUpperCase().trim())
                    .toRepresentation();
        }catch (NotFoundException exception){
            throw new IdentityException("Not Found: Role with name "+ userIdentity.getRole());
        }
        return roleRepresentation;
    }

    @Override
    public void logout(UserIdentity userIdentity) throws MeedlException {
        UserResource userResource = getUserResource(userIdentity);
        userResource.logout();
    }

    private void validateUserIdentity(UserIdentity userIdentity) throws MeedlException {
        log.info("Validating userIdentity object {}",userIdentity);
        MeedlValidator.validateObjectInstance(userIdentity);
    }
    private void validateUserIdentityDetails(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getEmail())
                || StringUtils.isEmpty(userIdentity.getFirstName())
                || StringUtils.isEmpty(userIdentity.getLastName())
                || userIdentity.getRole() == null
                || StringUtils.isEmpty(userIdentity.getRole().name()))
            throw new IdentityException(INVALID_REGISTRATION_DETAILS.getMessage());
        getRoleRepresentation(userIdentity);
    }
    private void validateUserIdentityDeleteDetails(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
    }

}
