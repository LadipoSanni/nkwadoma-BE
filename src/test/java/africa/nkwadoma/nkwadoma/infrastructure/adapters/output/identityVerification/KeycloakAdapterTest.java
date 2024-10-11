package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.TRAINEE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class KeycloakAdapterTest {
    @Autowired
    private IdentityManagerOutPutPort identityManagementOutputPort;
    private UserIdentity john;
    private UserIdentity peter;

    @BeforeEach
    void setUp() {
        john = new UserIdentity();
        john.setFirstName("John");
        john.setLastName("Max");
        john.setEmail("johnmax@lendspace.com");
        john.setRole(PORTFOLIO_MANAGER);

        peter = new UserIdentity();
        peter.setFirstName("Peter");
        peter.setLastName("Mark");
        peter.setEmail("peter@lendspace.com");
        peter.setRole(TRAINEE);
    }


    @Test
    @Order(1)
    void createUser() {
        try {
            UserIdentity createdUser = identityManagementOutputPort.createUser(john);
            identityManagementOutputPort.createUser(peter);
            assertNotNull(createdUser);
            assertNotNull(createdUser.getId());
            assertEquals(john.getId(), createdUser.getId());
            assertEquals(createdUser.getEmail(), john.getEmail());
            assertEquals(createdUser.getFirstName(), john.getFirstName());
            assertEquals(createdUser.getLastName(), john.getLastName());
        }catch (MiddlException exception){
            log.info("{} {}", exception.getClass().getName(),exception.getMessage());
        }
    }
    @Test
    void createUserWithNullUserIdentity(){
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(null));
    }
    @Test
    void createUserWithExistingEmail(){
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithEmptyStringEmail(){
        john.setEmail("");
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    void createUserWithInvalidUserRole(){
        john.setRole(null);
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    void createUserWithEmptyFirstName(){
        john.setFirstName(null);
        assertThrows(IdentityException.class,()-> identityManagementOutputPort.createUser(john));
    }
    @Test
    void createUserWithEmptyLastName(){
        john.setLastName(null);
        assertThrows(MiddlException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    @Order(2)
    void createPassword(){
        try {
            john.setPassword("passwordJ@345");
            identityManagementOutputPort.createPassword(john.getEmail(), john.getPassword());
        }catch (MiddlException e){
            log.info("{} {}",e.getClass().getName(),e.getMessage());
        }

    }

    @Test
    @Order(3)
    void login(){
        try {
            john.setPassword("passwordJ@345");
            identityManagementOutputPort.login(john);
        }catch (MiddlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }
    @Test
    void loginWithWrongDetails() throws MiddlException {

        john.setEmail("wrong@gmail.com");
        john.setPassword("passwordJ@345");
        john.setFirstName("wrong firstname");
        assertThrows(IdentityException.class,()->identityManagementOutputPort.login(john));
    }

    @Test
    @Order(4)
    void changePassword(){

        try {
            john.setNewPassword("neWpasswordJ@345");
            identityManagementOutputPort.changePassword(john);

            john.setPassword(john.getNewPassword());
            identityManagementOutputPort.login(john);


        }catch (MiddlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }

    @Test
    @Order(5)
    void enableAccountThatHasBeenEnabled() {
        assertThrows(MiddlException.class, () -> identityManagementOutputPort.enableUserAccount(john));

    }

    @Test
    void enableAccountWithWrongEmail() {
        john.setEmail("wrong@gmail.com");
        assertThrows(MiddlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
    }

    @Test
    @Order(6)
    void disAbleAccount() {
        try{
            identityManagementOutputPort.disableUserAccount(john);
        }catch (MiddlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }
    }


    @Test
    void getUserRepresentation()  {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            assertNotNull(userRepresentation);
            assertEquals(john.getFirstName(), userRepresentation.getFirstName());
            assertEquals(john.getLastName(), userRepresentation.getLastName());
        } catch (MiddlException e) {
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
        } catch (MiddlException e) {
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
        } catch (MiddlException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserRepresentationWithExactMatchForMultipleUsers()  {
        john.setEmail("lendspace.com");
        assertThrows(MiddlException.class,()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }

    @Test
    void getUserRepresentationThatDoesNotExist() {
        john.setEmail("noneexistinguser@example.com");
        assertThrows(
                MiddlException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }
    @Test
    void getUserRepresentationWithNullUserIdentity() {
        assertThrows(
                MiddlException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(null, Boolean.TRUE));
    }
    @Test
    void getUserResourceWithNullUserIdentity() {
        assertThrows(
                MiddlException.class,
                ()-> identityManagementOutputPort.getUserResource(null));
    }

    @Test
    void getUserResource() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            john.setId(userRepresentation.getId());
            UserResource  userResource = identityManagementOutputPort.getUserResource(john);
            assertNotNull(userResource);
            assertEquals(john.getEmail(), userResource.toRepresentation().getEmail());
        } catch (MiddlException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserResourceWithInvalidUserId() {
        try {
            john.setId("invalid user id");
            UserResource userResource = identityManagementOutputPort.getUserResource(john);
            assertThrows(NotFoundException.class,()->userResource.toRepresentation());
        } catch (MiddlException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getRoleResource() {
        try {
            john.setRole(PORTFOLIO_MANAGER);
            RoleRepresentation roleRepresentation = identityManagementOutputPort.getRoleRepresentation(john);
            assertNotNull(roleRepresentation);
            assertEquals(john.getRole().toString(), roleRepresentation.getName().toString());
        } catch (MiddlException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getRoleResourceWithInvalidRoleName() {
            john.setRole(null);
            assertThrows(MiddlException.class,()->identityManagementOutputPort.getRoleRepresentation(john));
    }
    @Test
    void deleteUser() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            john.setId(userRepresentation.getId());
            identityManagementOutputPort.deleteUser(john);
        } catch (MiddlException exception) {
            log.info(exception.getMessage());
        }
        assertThrows(MiddlException.class, ()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }
    @Test
    void deleteUserWithNullUserIdentity() {
        assertThrows(MiddlException.class,()-> identityManagementOutputPort.deleteUser(null));
    }
    @Test
    void deleteUserWithNullUserId() {
        assertThrows(MiddlException.class,()-> identityManagementOutputPort.deleteUser(john));
    }
    @Test
    void deleteUserWithInCorrectUserId() {
        john.setId("incorrect user id");
        assertThrows(MiddlException.class,()-> identityManagementOutputPort.deleteUser(john));
    }


    @AfterAll
    void cleanUp() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(peter, Boolean.TRUE);
            peter.setId(userRepresentation.getId());
            identityManagementOutputPort.deleteUser(peter);
        } catch (MiddlException exception) {
            log.info(exception.getMessage());
        }
    }
}
