package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator.validateOrganizationIdentity;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdapter implements IdentityManagerOutPutPort {
    private final Keycloak keycloak;
    @Value("${realm}")
    private String KEYCLOAK_REALM;

    private final KeyCloakMapper mapper;
    //private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;

    @Override
    public UserIdentity createUser(UserIdentity userIdentity) throws MiddlException{
        validateUserIdentityDetails(userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            Response response = users.create(userRepresentation);
            if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                throw new IdentityException(USER_IDENTITY_ALREADY_EXISTS);
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
    public void deleteUser(UserIdentity userIdentity) throws MiddlException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getId())) {
            log.error("User id is empty");
            throw new MiddlException("User does not exist");
        }
        UserResource userResource = getUserResource(userIdentity);
        try{
            userResource.remove();
        }catch (NotFoundException exception) {
            log.info("deleteUser called with invalid user id: {}", userIdentity.getId());
            throw new MiddlException("User does not exist");
        }

    }

    @Override
    public Optional<UserIdentity> getUserByEmail(String email) throws MiddlException {
        UserRepresentation userRepresentation = findUserByEmail(email);
        if (userRepresentation == null) {
            return Optional.empty();
        }
        UserIdentity userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);
        return Optional.of(userIdentity);
    }

    @Override
    public OrganizationIdentity createOrganization(OrganizationIdentity organizationIdentity) throws MiddlException {
        validateOrganizationIdentity(organizationIdentity);
        ClientRepresentation clientRepresentation = createClientRepresentation(organizationIdentity);
        Response response = keycloak.realm(KEYCLOAK_REALM).clients().create(clientRepresentation);
        if (response.getStatusInfo().equals(Response.Status.CREATED)) {
            clientRepresentation = getClientRepresentation(organizationIdentity);
            organizationIdentity.setId(clientRepresentation.getId());

            //now create user in keycloak
//            UserIdentity newUser = createUser(organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser());
//            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
//            employeeIdentity.setMiddlUser(newUser);
//            employeeIdentity.setOrganization(organizationIdentity);

//            UserIdentity newUser = createUser(organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser());
//            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
//            employeeIdentity.setMiddlUser(newUser);
//            employeeIdentity.setOrganization(organizationIdentity.getId());

        }else if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
            throw new MiddlException(CLIENT_EXIST);
        }
        return organizationIdentity;
    }

    public ClientRepresentation getClientRepresentation(OrganizationIdentity organizationIdentity) throws MiddlException {
        return keycloak.realm(KEYCLOAK_REALM)
                .clients()
                .findByClientId(organizationIdentity.getId())
                .stream().findFirst().orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND));
    }

    private ClientRepresentation createClientRepresentation(OrganizationIdentity organizationIdentity) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(organizationIdentity.getName());
        clientRepresentation.setName(organizationIdentity.getName());
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setPublicClient(true);
        return clientRepresentation;
    }


    public UserRepresentation findUserByEmail(String email) throws MiddlException {
        UserIdentityValidator.validateEmail(email);
        List<UserRepresentation> foundUsers = keycloak.realm(KEYCLOAK_REALM).users().search(email);
        return foundUsers.isEmpty() ? null : foundUsers.get(0);
    }

    private void assignRole(UserIdentity userIdentity) throws MiddlException {
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
    public UserRepresentation getUserRepresentation(UserIdentity userIdentity, boolean exactMatch) throws MiddlException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail(),exactMatch)
                .stream().findFirst().orElseThrow(()-> new IdentityException(USER_NOT_FOUND));
    }
    public UserResource getUserResource(UserIdentity userIdentity) throws MiddlException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .get(userIdentity.getId());
    }
    public RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws MiddlException {
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
    private void validateUserIdentity(UserIdentity userIdentity) throws MiddlException {
        log.info("Validating userIdentity {}",userIdentity);
        if (userIdentity == null)
            throw new IdentityException(INVALID_REGISTRATION_DETAILS);
    }
    private void validateUserIdentityDetails(UserIdentity userIdentity) throws MiddlException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getEmail())
                || StringUtils.isEmpty(userIdentity.getFirstName())
                || StringUtils.isEmpty(userIdentity.getLastName())
                || StringUtils.isEmpty(userIdentity.getRole()))
            throw new IdentityException(INVALID_REGISTRATION_DETAILS);
        getRoleRepresentation(userIdentity);
    }
    private void validateUserIdentityDeleteDetails(UserIdentity userIdentity) throws MiddlException {
        validateUserIdentity(userIdentity);
    }

}
