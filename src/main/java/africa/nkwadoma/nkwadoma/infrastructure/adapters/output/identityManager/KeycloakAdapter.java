package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.LearnSpaceUserException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
        UserRepresentation userRepresentation = mapper.map(userIdentity);
        userRepresentation.setUsername(userIdentity.getEmail());
        userRepresentation.setEmailVerified(userIdentity.isEmailVerified());
        userRepresentation.setEnabled(userIdentity.isEnabled());
        log.info("the realm is ...{}",  KEYCLOAK_REALM);

        try{
            UsersResource users = keycloak.realm(KEYCLOAK_REALM).users();
            Response response = users.create(userRepresentation);
            if (response.getStatusInfo().equals(Response.Status.CONFLICT)) {
                throw new InfrastructureException("UserIdentity already exists");
            }
            UserRepresentation createdUserRepresentation = getUserRepresentation(userIdentity.getEmail(), Boolean.TRUE);
            userIdentity.setUserId(createdUserRepresentation.getId());
            log.info("The user id is ........{} ---->  ", userIdentity.getUserId());

//            assignRole(userRegistrationRequest);
//            userRepresentation = keycloak.realm(KEYCLOAK_REALM).users().search(userRegistrationRequest.getEmail()).get(0);
        } catch (NotFoundException exception) {
            throw new InfrastructureException(exception.getMessage());
        }
//        return userRepresentation;
     return null;
    }

    private void assignRole(UserIdentity userIdentity) throws InfrastructureException {
        String email = userIdentity.getEmail();
        String role = userIdentity.getRole();

        try {
            List<UserRepresentation> users = getUserRepresentations(email);
            if (users.isEmpty()) throw new InfrastructureException("User does not exist");
            UserRepresentation user = users.get(BigDecimal.ZERO.intValue());

            RoleRepresentation roleRepresentation = keycloak
                                                        .realm(KEYCLOAK_REALM)
                                                        .roles()
                                                        .get(role.toUpperCase().trim())
                                                        .toRepresentation();
            if (roleRepresentation == null) throw new InfrastructureException("Role not found: " + role.toUpperCase());

            UserResource userResource = keycloak
                    .realm(KEYCLOAK_REALM)
                    .users()
                    .get(user.getId());

            userResource.roles()
                    .realmLevel()
                    .add(List.of(roleRepresentation));
        } catch (NotFoundException | InfrastructureException exception) {
            throw new InfrastructureException("Resource not found: " + exception.getMessage());
        }
    }

    public List<UserRepresentation> getUserRepresentations(String email) {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(email);
    }
    public UserRepresentation getUserRepresentation(String email, boolean exactMatch) throws InfrastructureException {
        return keycloak
                .realm(KEYCLOAK_REALM)
                .users()
                .search(email,exactMatch)
                .stream().findFirst().orElseThrow(()-> new InfrastructureException("User not found"));
    }

}
