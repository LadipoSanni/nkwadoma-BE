package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.TRAINEE;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMPTY_INPUT_FIELD_ERROR;
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
        }catch (MeedlException exception){
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
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.createUser(john));
    }

    @Test
    void createPassword(){
        try {
            Optional<UserIdentity> existingUser = identityManagementOutputPort.getUserByEmail(john.getEmail());
            assertTrue(existingUser.isPresent());
            assertFalse(existingUser.get().isEnabled());
            assertFalse(existingUser.get().isEmailVerified());

            john.setPassword("passwordJ@345");
            UserIdentity userIdentity = identityManagementOutputPort.createPassword(john.getEmail(), john.getPassword());

            assertNotNull(userIdentity);
            assertNotNull(userIdentity.getId());
            assertTrue(userIdentity.isEmailVerified());
            assertTrue(userIdentity.isEnabled());
            userIdentity.setPassword(john.getPassword());

            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(userIdentity);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }catch (MeedlException e){
            log.info("{} {}",e.getClass().getName(),e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"    ", StringUtils.SPACE, StringUtils.EMPTY})
    void createPasswordWithNullPassword(String password) {
        MeedlException meedlException = assertThrows(MeedlException.class, () ->
                identityManagementOutputPort.createPassword(john.getEmail(), password));
        assertEquals(EMPTY_INPUT_FIELD_ERROR.getMessage(), meedlException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"fgdgffdfdfdf    ", "    dddfdsfdsfsfd"})
    void createPasswordWithSpaces(String password) {
        try {

            UserIdentity userIdentity = identityManagementOutputPort.createPassword(john.getEmail(), password);
            assertNotNull(userIdentity);
            assertNotNull(userIdentity.getId());
            assertTrue(userIdentity.isEmailVerified());
            assertTrue(userIdentity.isEnabled());
            userIdentity.setPassword(password);

            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(userIdentity);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        } catch (MeedlException e) {
            log.error("Failed to create password", e);
        }
    }

    @Test
    void login(){
        try {
            john.setPassword("passwordJ@345");
            identityManagementOutputPort.createPassword(john.getEmail(), john.getPassword());
            identityManagementOutputPort.login(john);
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }
    @Test
    void loginWithWrongDetails() {

        john.setEmail("wrong@gmail.com");
        john.setPassword("passwordJ@345");
        john.setFirstName("wrong firstname");
        assertThrows(IdentityException.class,()->identityManagementOutputPort.login(john));
    }

    @Test
    void changePassword(){

        try {
            john.setNewPassword("neWpasswordJ@345");
            identityManagementOutputPort.changePassword(john);

            john.setPassword(john.getNewPassword());
            identityManagementOutputPort.login(john);
            //TODO include asserts

        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void enableAccountThatHasBeenEnabled() {
        try {
            UserIdentity userIdentity = identityManagementOutputPort.enableUserAccount(john);
            assertTrue(userIdentity.isEnabled());
            assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
        } catch (MeedlException e) {
            log.info("{}", e.getMessage());
        }
    }

    @Test
    void enableAccountWithWrongEmail() {
        john.setEmail("wrong@gmail.com");
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
    }

    @Test
    void disAbleAccount() {
        try{
            //TODO: check account is enabled

            identityManagementOutputPort.disableUserAccount(john);

            //TODO assert account is disabled
        }catch (MeedlException exception){
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
        } catch (MeedlException e) {
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
        } catch (MeedlException e) {
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
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserRepresentationWithExactMatchForMultipleUsers()  {
        john.setEmail("lendspace.com");
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }

    @Test
    void getUserRepresentationThatDoesNotExist() {
        john.setEmail("noneexistinguser@example.com");
        assertThrows(
                MeedlException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }
    @Test
    void getUserRepresentationWithNullUserIdentity() {
        assertThrows(
                MeedlException.class,
                ()-> identityManagementOutputPort.getUserRepresentation(null, Boolean.TRUE));
    }
    @Test
    void getUserResourceWithNullUserIdentity() {
        assertThrows(
                MeedlException.class,
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
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getUserResourceWithInvalidUserId() {
        try {
            john.setId("invalid user id");
            UserResource userResource = identityManagementOutputPort.getUserResource(john);
            assertThrows(NotFoundException.class,()->userResource.toRepresentation());
        } catch (MeedlException e) {
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
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }
    @Test
    void getRoleResourceWithInvalidRoleName() {
            john.setRole(null);
            assertThrows(MeedlException.class,()->identityManagementOutputPort.getRoleRepresentation(john));
    }
    @Test
    void deleteUser() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE);
            john.setId(userRepresentation.getId());
            identityManagementOutputPort.deleteUser(john);
        } catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.getUserRepresentation(john, Boolean.TRUE));
    }
    @Test
    void deleteUserWithNullUserIdentity() {
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.deleteUser(null));
    }
    @Test
    void deleteUserWithNullUserId() {
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.deleteUser(john));
    }
    @Test
    void deleteUserWithInCorrectUserId() {
        john.setId("incorrect user id");
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.deleteUser(john));
    }


    @AfterAll
    void cleanUp() {
        try {
            UserRepresentation userRepresentation = identityManagementOutputPort.getUserRepresentation(peter, Boolean.TRUE);
            peter.setId(userRepresentation.getId());
            identityManagementOutputPort.deleteUser(peter);
        } catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
    }
}
