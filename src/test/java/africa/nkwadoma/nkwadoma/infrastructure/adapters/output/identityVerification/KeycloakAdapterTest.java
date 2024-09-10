package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class KeycloakAdapterTest {
    @Autowired
    private Keycloak keycloak;
    @Value("${realm}")
    private String KEYCLOAK_REALM;
    @Autowired
    private KeyCloakMapper mapper;

    @Autowired
    private IdentityManagerOutPutPort identityManagementOutputPort;
    @Autowired
    private KeycloakAdapter keycloakAdapter;
    private UserIdentity userIdentity;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("John");
        userIdentity.setLastName("Doe");
        userIdentity.setEmail("dogs@esterace.com");
    }

    @Test
    void createUser() throws InfrastructureException {
        UserIdentity createdUser = identityManagementOutputPort.createUser(userIdentity);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
        assertEquals(createdUser.getEmail(), userIdentity.getEmail());
        assertEquals(createdUser.getFirstName(), userIdentity.getFirstName());
        assertEquals(createdUser.getLastName(), userIdentity.getLastName());
    }
    @Test
    void doubleRegistrationNotAllowed(){
//              identityManagementOutputPort.createUser(userIdentity);
      InfrastructureException exception = assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(userIdentity));
      assertEquals(exception.getMessage(), "UserIdentity already exists");
    }

    @Test
    void getUserRepresentation()  {
        try {
            UserRepresentation userRepresentation = keycloakAdapter.getUserRepresentation(userIdentity.getEmail(), Boolean.TRUE);
            assertNotNull(userRepresentation);
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserRepresentationWithSimilarEmail()  {
        try {
            UserRepresentation userRepresentation = keycloakAdapter.getUserRepresentation("esterace.com", Boolean.FALSE);
            assertNotNull(userRepresentation);
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserRepresentationWithSimilarEmailReturnsFirstUser()  {
        try {
            UserRepresentation userRepresentation = keycloakAdapter.getUserRepresentation("esterace.com", Boolean.TRUE);
            assertEquals("cats@esterace.com", userRepresentation.getEmail());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserRepresentationWithTheSameEmail()  {
        try {
            UserRepresentation userRepresentation = keycloakAdapter.getUserRepresentation(userIdentity.getEmail(), Boolean.TRUE);
            assertEquals(userIdentity.getEmail(), userRepresentation.getEmail());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserRepresentationThatDoesNotExist() {
        userIdentity.setEmail("noneexistinguser@example.com");
        assertThrows(
                InfrastructureException.class,
                ()-> keycloakAdapter.getUserRepresentation(userIdentity.getEmail(), Boolean.TRUE));
    }
}