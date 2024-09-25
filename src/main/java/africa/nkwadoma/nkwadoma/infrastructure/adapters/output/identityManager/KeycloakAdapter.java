package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdapter implements IdentityManagerOutPutPort {
    private final Keycloak keycloak;
    @Value("${realm}")
    private String KEYCLOAK_REALM;

    private final KeyCloakMapper mapper;


    @Override
    public UserIdentity createUser(UserIdentity userIdentity) throws InfrastructureException{
        validateUserIdentityDetails(userIdentity);
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            Response response = users.create(userRepresentation);
            if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                throw new InfrastructureException("UserIdentity already exists");
            }
            UserRepresentation createdUserRepresentation = getUserRepresentation(userIdentity, Boolean.TRUE);
            userIdentity.setUserId(createdUserRepresentation.getId());

            assignRole(userIdentity);
            userIdentity =  mapper.mapUserRepresentationToUserIdentity(createdUserRepresentation);
        } catch (NotFoundException exception) {
            throw new InfrastructureException(exception.getMessage());
        }
        return userIdentity;
    }
    @Override
    public void deleteUser(UserIdentity userIdentity) throws InfrastructureException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getUserId())) {
            log.error("User id is empty");
            throw new InfrastructureException("User does not exist");
        }
        UserResource userResource = getUserResource(userIdentity);
        try{
            userResource.remove();
        }catch (NotFoundException exception) {
            log.info("deleteUser called with invalid user id: {}", userIdentity.getUserId());
            throw new InfrastructureException("User does not exist");
        }

    }

    @Override
    public Optional<UserIdentity> getUserByEmail(String email) throws MiddlException {
        UserRepresentation userRepresentation = findUserByEmail(email);
        if (userRepresentation == null) {
            return Optional.empty();
        }
        UserIdentity userIdentity = mapper.mapUserRepresentationToUserIdentity(userRepresentation);
        userIdentity.setUserRepresentation(userRepresentation);
        return Optional.of(userIdentity);
    }


    public UserRepresentation findUserByEmail(String email) throws MiddlException {
        UserIdentityValidator.validateEmail(email);
        List<UserRepresentation> foundUsers = keycloak.realm(KEYCLOAK_REALM).users().search(email);
        return foundUsers.isEmpty() ? null : foundUsers.get(0);
    }

    private void assignRole(UserIdentity userIdentity) throws InfrastructureException {
        try {
            RoleRepresentation roleRepresentation = getRoleRepresentation(userIdentity);
            UserResource userResource = getUserResource(userIdentity);
            userResource.roles().realmLevel().add(List.of(roleRepresentation));
        } catch (NotFoundException | InfrastructureException exception) {
            throw new InfrastructureException(String.format("Resource not found: %s", exception.getMessage()));
        }
    }

    public List<UserRepresentation> getUserRepresentations(UserIdentity userIdentity) {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail());
    }
    public UserRepresentation getUserRepresentation(UserIdentity userIdentity, boolean exactMatch) throws InfrastructureException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(userIdentity.getEmail(),exactMatch)
                .stream().findFirst().orElseThrow(()-> new InfrastructureException("User not found"));
    }
    public UserResource getUserResource(UserIdentity userIdentity) throws InfrastructureException {
        validateUserIdentity(userIdentity);
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .get(userIdentity.getUserId());
    }
    public RoleRepresentation getRoleRepresentation(UserIdentity userIdentity) throws InfrastructureException {
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = keycloak
                    .realm(KEYCLOAK_REALM)
                    .roles()
                    .get(userIdentity.getRole().toUpperCase().trim())
                    .toRepresentation();
        }catch (NotFoundException exception){
            throw new InfrastructureException("Not Found: Role with name "+ userIdentity.getRole());
        }
        return roleRepresentation;
    }
    private void validateUserIdentity(UserIdentity userIdentity) throws InfrastructureException {
        log.info("Validating userIdentity {}",userIdentity);
        if (userIdentity == null)
            throw new InfrastructureException("Invalid registration details");
    }
    private void validateUserIdentityDetails(UserIdentity userIdentity) throws InfrastructureException {
        validateUserIdentity(userIdentity);
        if (StringUtils.isEmpty(userIdentity.getEmail())
                || StringUtils.isEmpty(userIdentity.getFirstName())
                || StringUtils.isEmpty(userIdentity.getLastName())
                || StringUtils.isEmpty(userIdentity.getRole()))
            throw new InfrastructureException("Invalid registration details");
        getRoleRepresentation(userIdentity);
    }
    private void validateUserIdentityDeleteDetails(UserIdentity userIdentity) throws InfrastructureException {
        validateUserIdentity(userIdentity);
    }
}
