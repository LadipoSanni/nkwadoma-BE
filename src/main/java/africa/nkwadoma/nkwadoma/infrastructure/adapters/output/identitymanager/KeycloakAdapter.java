package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.KeyCloakMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.util.*;
import org.springframework.web.client.*;


import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;

@RequiredArgsConstructor
@Slf4j
public class KeycloakAdapter implements IdentityManagerOutputPort {
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

    @Value("${keycloak.refresh-token.url}")
    private String refreshTokenUrl;


    @Override
    public UserIdentity createUser(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentityDetails(userIdentity);
        log.info("Done validating user identity details in keycloak adapter : {}",userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            try (Response response = users.create(userRepresentation)) {
                if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                    log.error("{} - {} --- Error occurred on attempting to create user on keycloak", Response.Status.CONFLICT, USER_IDENTITY_ALREADY_EXISTS.getMessage());
                    throw new IdentityException(USER_IDENTITY_ALREADY_EXISTS.getMessage());
                }
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
    public UserIdentity updateUserData(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentityDetails(userIdentity);
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        log.info("Done validating user identity details in keycloak adapter for update : {}",userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        UserIdentity foundUserIdentity = getUserById(userIdentity.getId());
        userIdentity.setEmail(foundUserIdentity.getEmail());
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            users.get(userRepresentation.getId()).update(userRepresentation);

//            assignRole(userIdentity);
//            log.info("User created on keycloak, role assigned : {}", userIdentity.getId());
        } catch (NotFoundException exception) {
            log.error("{} - {} --- Error occurred on attempting to update user details on keycloak", exception.getClass().getName(), exception.getMessage());
            throw new IdentityException(exception.getMessage());
        }
        return userIdentity;
    }

    @Override
    public void deleteUser(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), "Please provide a valid user identification");
        UserResource userResource = getUserResource(userIdentity);
        try{
            userResource.remove();
            log.info("User deleted on keycloak: {}", userIdentity.getId());
        }catch (NotFoundException exception) {
            log.info("deleteUser called with invalid user id: {}", userIdentity.getId());
            throw new MeedlException("User does not exist");
        }
    }

    @Override
    public Optional<UserIdentity> getUserByEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        List<UserRepresentation> foundUsers = getUserRepresentations(email);
        if (foundUsers.isEmpty()) {
            log.warn("Could not find user with email {}", email);
            return Optional.empty();
        }
        UserRepresentation userRepresentation = foundUsers.get(0);
        UserIdentity userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);
        log.info("Found user with email {} ", email);
        return Optional.of(userIdentity);
    }
    @Override
    public IdentityRole getUserRoles(UserIdentity userIdentity) throws MeedlException {
        log.info("Getting user roles");
        try {
            UserResource userResource = keycloak
                    .realm(KEYCLOAK_REALM)
                    .users()
                    .get(userIdentity.getId());

            List<RoleRepresentation> roles = userResource
                    .roles()
                    .realmLevel()
                    .listAll();

            String userRole;
            if (roles.isEmpty()) {
                log.error("User with ID {} has no roles assigned in realm {}", userIdentity.getId(), KEYCLOAK_REALM);
                throw new MeedlException("User has no role");
            } else if (roles.size() <= 2) {
                userRole = roles.get(0).getName();
                log.info("The user role 0 is {}", userRole);
                IdentityRole identityRole = checkIfUserRoleIsMeedlRole(userRole);
                log.info("First role checked is {}", identityRole);
                if (!ObjectUtils.isEmpty(identityRole)){
                    return identityRole;
                }
                userRole = roles.get(1).getName();
                log.info("The user role 1 is {}", userRole);
                identityRole = checkIfUserRoleIsMeedlRole(userRole);
                log.info("Second role checked is {}", identityRole);
                return identityRole;
            } else {
                roles.forEach(role ->
                        log.info("User with ID {} has role: {}", userIdentity.getId(), role.getName())
                );
                throw new MeedlException("User has more than one role");
            }
        } catch (Exception e) {
            log.error("Failed to fetch roles for user {}: {}", userIdentity.getId(), e.getMessage(), e);
            throw new MeedlException("Failed to fetch roles for user");
        }
    }

    private IdentityRole checkIfUserRoleIsMeedlRole(String role) {
        if(IdentityRole.isValidRole(role)){
            log.info("The role matched {}", role.toUpperCase());
            return IdentityRole.fromString(role.toUpperCase());
        }else {
            return null;
        }
    }

    @Override
    public OrganizationIdentity createKeycloakClient(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_STATUS_MUST_NOT_BE_EMPTY.getMessage());
        organizationIdentity.validate();
        log.info("Keycloak service validated organization ... {}", organizationIdentity);
        ClientRepresentation clientRepresentation = createClientRepresentation(organizationIdentity);
        try (Response response = getClients(keycloak).create(clientRepresentation)) {
            if (response.getStatusInfo().equals(Response.Status.CREATED)) {
                clientRepresentation = getClientRepresentationByName(organizationIdentity.getName());
                organizationIdentity.setId(clientRepresentation.getId());
                log.info("Client created successfully. Name: {}", organizationIdentity.getName());
            } else if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                log.error("{} - Client already exists.", response.getStatusInfo());
                throw new MeedlException(CLIENT_EXIST.getMessage());
            }
        }
        return organizationIdentity;
    }
    @Override
    public void enableClient(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        ClientRepresentation clientRepresentation = getClientRepresentationByClientId(organizationIdentity.getName());
        log.info("ClientRepresentation {} {}", clientRepresentation.getName() , clientRepresentation.getId());
        clientRepresentation.setEnabled(Boolean.TRUE);

        ClientResource clientResource = getClientResource(organizationIdentity.getId());
        clientResource.update(clientRepresentation);
        log.info("Client enabled on keycloak {}", organizationIdentity.getName());
    }
    @Override
    public void disableClient(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, IdentityMessages.IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getId(), "Please provide a valid organization identification.");
        ClientRepresentation clientRepresentation = getClientRepresentationByClientId(organizationIdentity.getName());
        log.info("ClientRepresentation {} {}", clientRepresentation.getName() , clientRepresentation.getId());
        clientRepresentation.setEnabled(Boolean.FALSE);

        ClientResource clientResource = getClientResource(organizationIdentity.getId());
        clientResource.update(clientRepresentation);
        log.info("Client disabled on keycloak {}", organizationIdentity.getName());
    }
    @Override
    public ClientRepresentation getClientRepresentationByClientId(String clientName) throws MeedlException {
        MeedlValidator.validateDataElement(clientName, "Organization name is required");
        List<ClientRepresentation> clientRepresentations = getClients(keycloak).findByClientId(clientName);
        if (clientRepresentations.isEmpty()) throw new MeedlException(CLIENT_NOT_FOUND.getMessage());
        return clientRepresentations.get(0);
    }

    private ClientsResource getClients(Keycloak keycloak) {
        return keycloak.realm(KEYCLOAK_REALM).clients();
    }

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validatePassword(userIdentity.getPassword());
        log.info("User login credentials: {}", userIdentity.getEmail());
        try {
            Keycloak keycloakClient = getKeycloak(userIdentity);
            TokenManager tokenManager = keycloakClient.tokenManager();
            log.info("Login successful for user {}", userIdentity.getEmail());
            return tokenManager.getAccessToken();
        } catch (NotAuthorizedException | BadRequestException exception ) {
            log.info("Error logging in user: {}", exception.getMessage());
            throw new IdentityException(IdentityMessages.INVALID_EMAIL_OR_PASSWORD.getMessage());
        }
    }

    @Override
    public AccessTokenResponse refreshToken(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getRefreshToken(), UserMessages.REFRESH_TOKEN_CANNOT_BE_EMPTY.getMessage());

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", CLIENT_ID);
            body.add("client_secret", CLIENT_SECRET);
            body.add("grant_type", OAuth2Constants.REFRESH_TOKEN);
            body.add("refresh_token", userIdentity.getRefreshToken());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(refreshTokenUrl, HttpMethod.POST, request, AccessTokenResponse.class);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new MeedlException(UserMessages.INVALID_REFRESH_TOKEN.getMessage());
        }
    }

    @Override
    public UserIdentity createPassword(UserIdentity userIdentity) throws MeedlException {
        validateEmailAndPassword(userIdentity.getEmail(), userIdentity.getPassword());
        String email = userIdentity.getEmail().trim();
        String password = userIdentity.getPassword().trim();
        UserIdentity foundUserIdentity = getUserByEmail(email)
                .orElseThrow(() -> new IdentityException(IdentityMessages.USER_NOT_FOUND.getMessage()));
        foundUserIdentity.setNewPassword(password);
        log.info("User ID for user creating password : {}", foundUserIdentity.getId());
        if (foundUserIdentity.isEmailVerified() && foundUserIdentity.isEnabled()) {
            log.error("User already verified can not create new password for this user {}", foundUserIdentity.getEmail());
            throw new IdentityException(USER_PREVIOUSLY_VERIFIED.getMessage());
        }
        foundUserIdentity = enableUserAccount(foundUserIdentity);
        setPassword(foundUserIdentity);
        foundUserIdentity.setPassword(password);
        foundUserIdentity.setEmail(email);
        AccessTokenResponse response = login(foundUserIdentity);
        foundUserIdentity.setAccessToken(response.getToken());
        foundUserIdentity.setRefreshToken(response.getRefreshToken());
        return foundUserIdentity;
    }

    @Override
    public void resetPassword(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        validateEmailAndPassword(userIdentity.getEmail(), userIdentity.getNewPassword());
        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail().trim())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        if (!(foundUser.isEmailVerified() && foundUser.isEnabled())){
            log.error("User not verified {} because email verified is {}, and user enabled is {}", foundUser.getEmail(), foundUser.isEmailVerified(), foundUser.isEnabled());
            throw new IdentityException(USER_NOT_VERIFIED.getMessage());
        }
        foundUser.setNewPassword(userIdentity.getNewPassword());
        setPassword(foundUser);
    }
    @Override
    public void setPassword(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validatePassword(userIdentity.getNewPassword());
        CredentialRepresentation credential = createCredentialRepresentation(userIdentity.getNewPassword());
        UserResource userResource = getUserResource(userIdentity);
        userResource.resetPassword(credential);
    }
    private static CredentialRepresentation createCredentialRepresentation(String password)  {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }
    @Override
    public UserIdentity verifyUserExistsAndIsEnabled(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        UserRepresentation userRepresentation = getUserRepresentation(userIdentity, Boolean.TRUE);
        MeedlValidator.validateUUID(userRepresentation.getId(),"Please provide a valid identification for the representation of this user to verify.");
        if (!(userRepresentation.isEnabled() && userRepresentation.isEmailVerified())){
            log.error("User with email {} is not enabled {} or email is not verified {}", userRepresentation.getEmail(), userRepresentation.isEnabled(), userRepresentation.isEmailVerified());
            throw new MeedlException(MeedlMessages.USER_NOT_ENABLED.getMessage());
        }
        log.info("User with email {} exist and is endabled.", userRepresentation.getEmail());
        userIdentity.setId(userRepresentation.getId());
        return userIdentity;
    }
    @Override
    public UserIdentity enableUserAccount(UserIdentity userIdentity) throws MeedlException {
        log.info("Enable user account verification started {} ", userIdentity);
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateEmail(userIdentity.getEmail());
        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail().trim())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        if (foundUser.isEnabled()) {
            throw new IdentityException(ACCOUNT_ALREADY_ENABLED.getMessage());
        }

        List<UserRepresentation> userRepresentations = getUserRepresentations(foundUser);
        for (UserRepresentation userRepresentation : userRepresentations){
            userRepresentation.setEnabled(true);
            UserResource userResource = getUserResourceByKeycloakId(userRepresentation.getId());
            userResource.update(userRepresentation);
        }
        UserRepresentation userRepresentation = mapper.map(foundUser);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.TRUE);
        UserResource userResource = getUserResource(userIdentity);
        userResource.update(userRepresentation);
        userIdentity.setEnabled(Boolean.TRUE);
        userIdentity.setEmailVerified(Boolean.TRUE);
        log.info("After enabling on keycloak {}", userIdentity);
        return userIdentity;
    }

    @Override
    public UserIdentity disableUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        log.info("validate user email  {}", userIdentity.getEmail());
        MeedlValidator.validateEmail(userIdentity.getEmail());
        log.info("Validating deactivation reason {}", userIdentity.getDeactivationReason());
        MeedlValidator.validateDataElement(userIdentity.getDeactivationReason(), "Deactivation reason required");

        UserIdentity foundUser = getUserByEmail(userIdentity.getEmail().trim())
                .orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));

        if (!foundUser.isEnabled()) {
            log.warn("The status of the found user is...  {} id : {}", Boolean.FALSE, foundUser.getId() );
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
    @Override
    public UserIdentity getUserById(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        return mapper.mapUserRepresentationToUserIdentity(getUserRepresentationById(userId));
    }


    private UserRepresentation getUserRepresentationById(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        UserResource userResource = keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .get(userId);
        UserRepresentation userRepresentation;
        try {
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            log.error("User not found on keycloak. User id: {}. Error message : {}", userId, e.getMessage());
            throw new IdentityException("Please register on our platform or contact your admin.");
        }
        return userRepresentation;
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
    public boolean userExistByEmail(String userEmail) {
        try {
            getUserRepresentation(UserIdentity.builder().email(userEmail).build(), Boolean.TRUE);
            log.info("User with email {}, exist on keycloak", userEmail);
        } catch (MeedlException e) {
            log.error("Error occurred verifying user exist by email representation ", e);
            return Boolean.FALSE;
        }
            return Boolean.TRUE;

    }

    @Override
    public boolean clientExistByName(String organizationName) {
        ClientRepresentation clientRepresentation;
        try {
            clientRepresentation = getClientRepresentationByClientId(organizationName);
        } catch (MeedlException e) {
            log.error("Error occurred verifying client exist by organization name representation ", e);
            return Boolean.FALSE;
        }
        if (!ObjectUtils.isEmpty(clientRepresentation)){
            log.info("Client with name {}, exist on keycloak", organizationName);
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
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
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateEmail(userIdentity.getEmail());
        log.info("Getting user representation for user with email {}", userIdentity.getEmail());
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail(), exactMatch)
                .stream().findFirst().orElseThrow(()-> new IdentityException(USER_NOT_FOUND.getMessage()));
    }
    public UserResource getUserResource(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .get(userIdentity.getId());
    }
    public RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MeedlException {
        if (userIdentity.getRole() == null || StringUtils.isEmpty(userIdentity.getRole().name()))
            throw new IdentityException(INVALID_ROLE.getMessage());
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloak
                    .realm(KEYCLOAK_REALM)
                    .roles()
                    .get(userIdentity.getRole().name().toUpperCase().trim())
                    .toRepresentation();
        }catch (NotFoundException exception){
            throw new IdentityException("Not Found: Role with name "+ userIdentity.getRole().getRoleName());
        }
        return roleRepresentation;
    }
    @Override
    public List<UserIdentity> getUsersByRole(String roleName) throws MeedlException {
        MeedlValidator.validateDataElement(roleName, "Role name cannot be empty");

        try {
            List<UserRepresentation> users = keycloak
                    .realm(KEYCLOAK_REALM)
                    .roles()
                    .get(roleName.toUpperCase().trim())
                    .getUserMembers(0, Integer.MAX_VALUE);

            return users.stream()
                    .map(mapper::mapUserRepresentationToUserIdentity)
                    .toList();
        } catch (NotFoundException e) {
            throw new IdentityException("Role not found: " + roleName);
        }
    }
    @Override
    public void changeUserRole(UserIdentity userIdentity, String newRole) throws MeedlException {
        log.info("Changing user role from {} to {}. User Id {}", userIdentity.getRole(), newRole, userIdentity.getId());
        UserResource userResource = getUserResource(userIdentity);
        List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();
        if (!currentRoles.isEmpty()) {
            userResource.roles().realmLevel().remove(currentRoles);
        }

        RoleRepresentation newRoleRep = keycloak.realm(KEYCLOAK_REALM)
                .roles()
                .get(newRole.toUpperCase().trim())
                .toRepresentation();

        userResource.roles().realmLevel().add(List.of(newRoleRep));
        log.info("Updated role for user {} to {}", userIdentity.getEmail(), newRole);
    }



    @Override
    public void logout(UserIdentity userIdentity) throws MeedlException {
        UserResource userResource = getUserResource(userIdentity);
        userResource.logout();
    }

    private void validateUserIdentityDetails(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        if (StringUtils.isEmpty(userIdentity.getEmail())
                || StringUtils.isEmpty(userIdentity.getFirstName())
                || StringUtils.isEmpty(userIdentity.getLastName())
                || userIdentity.getRole() == null
                || StringUtils.isEmpty(userIdentity.getRole().name()))
            throw new IdentityException(INVALID_REGISTRATION_DETAILS.getMessage());
        getRoleRepresentation(userIdentity);
    }
    private void validateEmailAndPassword(String email, String password) throws MeedlException {
        MeedlValidator.validateEmail(email);
        MeedlValidator.validatePassword(password);
    }

}
