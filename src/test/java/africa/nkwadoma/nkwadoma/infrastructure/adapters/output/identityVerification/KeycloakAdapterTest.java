package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    private UserIdentity john;
    private UserIdentity peter;

    @BeforeEach
    void setUp() {
        john = new UserIdentity();
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setEmail("john@lendspace.com");
        john.setRole("PORTFOLIO_MANAGER");

        peter = new UserIdentity();
        peter.setFirstName("Peter");
        peter.setLastName("Mark");
        peter.setEmail("peter@lendspace.com");
        peter.setRole("TRAINEE");
    }

    @Test
    void createUser() throws InfrastructureException {
        UserIdentity createdUser = identityManagementOutputPort.createUser(john);
        identityManagementOutputPort.createUser(peter);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
        assertEquals(john.getUserId(), createdUser.getUserId());
        assertEquals(createdUser.getEmail(), john.getEmail());
        assertEquals(createdUser.getFirstName(), john.getFirstName());
        assertEquals(createdUser.getLastName(), john.getLastName());
    }
    @Test
    void createUserWithNoUserRole(){
        john.setRole(null);
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithInvalidUserRole(){
        john.setRole("INVALID_ROLE");
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    void createUserWithExistingEmail(){
       assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithNullUserIdentity(){
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(null));
    }
    @Test
    void createUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithEmptyStringEmail(){
        john.setEmail("");
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithEmptyFirstName(){
        john.setFirstName(null);
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithEmptyLastName(){
        john.setLastName(null);
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    void getUserRepresentation()  {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            assertNotNull(userRepresentation);
            assertEquals(john.getFirstName(), userRepresentation.getFirstName());
            assertEquals(john.getLastName(), userRepresentation.getLastName());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserRepresentationWithoutExactMatch()  {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.FALSE);
            assertNotNull(userRepresentation);
            assertEquals(john.getFirstName(), userRepresentation.getFirstName());
            assertEquals(john.getLastName(), userRepresentation.getLastName());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserRepresentationWithSimilarEmail()  {
        john.setEmail("lendspace.com");
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.FALSE);
            assertNotNull(userRepresentation);
            assertEquals(john.getFirstName(), userRepresentation.getFirstName());
            List<UserRepresentation> userRepresentations = identityManagementOutputPort.getUserRepresentations(john);
            assertNotNull(userRepresentations);
            assertEquals(2, userRepresentations.size());
            assertEquals(userRepresentation.getId(), userRepresentations.get(0).getId());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserRepresentationWithExactMatchForMultipleUsers()  {
        john.setEmail("lendspace.com");
        assertThrows(InfrastructureException.class,()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }

    @Test
    void getUserRepresentationThatDoesNotExist() {
        john.setEmail("noneexistinguser@example.com");
        assertThrows(
                InfrastructureException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }
    @Test
    void getUserRepresentationWithNullUserIdentity() {
        assertThrows(
                InfrastructureException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(null, Boolean.TRUE));
    }
    @Test
    void getUserResourceWithNullUserIdentity() {
        assertThrows(
                InfrastructureException.class,
                ()-> identityManagementOutputPort.getUserResource(null));
    }

    @Test
    void getUserResource() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            john.setUserId(userRepresentation.getId());
            UserResource  userResource = identityManagementOutputPort.getUserResource(john);
            assertNotNull(userResource);
            assertEquals(john.getEmail(), userResource.toRepresentation().getEmail());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserResourceWithInvalidUserId() {
        try {
            john.setUserId("invalid user id");
            UserResource userResource = identityManagementOutputPort.getUserResource(john);
            assertThrows(NotFoundException.class,()->userResource.toRepresentation());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getRoleResource() {
        try {
            john.setRole("PORTFOLIO_MANAGER");
            RoleRepresentation roleRepresentation = identityManagementOutputPort.getRoleRepresentation(john);
            assertNotNull(roleRepresentation);
            assertEquals(john.getRole(), roleRepresentation.getName());
        } catch (InfrastructureException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getRoleResourceWithInvalidRoleName() {
            john.setRole("INVALID_ROLE");
            assertThrows(InfrastructureException.class,()->identityManagementOutputPort.getRoleRepresentation(john));
    }
}