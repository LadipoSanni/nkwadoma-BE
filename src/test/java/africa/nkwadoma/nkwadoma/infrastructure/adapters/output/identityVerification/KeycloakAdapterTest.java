package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
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
    private String johnId;
    private boolean enabled;
    private final String password = "This-P@ssw0rd-Is-USed-In-Both-Ch@nge-and-CreatePassword";
    private OrganizationIdentity rizzGallery;
    private String rizzGalleryId ;

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

        OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setMeedlUser(peter);

        List<OrganizationEmployeeIdentity> employeePeter = new ArrayList<>();
        employeePeter.add(employeeIdentity);

        rizzGallery = new OrganizationIdentity();
//        rizzGallery.setId("5bc2ef97-1035-4e42-bc8b-22a90b809f7c");
        rizzGallery.setName("Rizz Gallery");
        rizzGallery.setEmail("rizzyJane@gmail.com");
        rizzGallery.setTin("7682-5627");
        rizzGallery.setRcNumber("RC87899");
        rizzGallery.setServiceOfferings(List.of(new ServiceOffering()));
        rizzGallery.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        rizzGallery.setPhoneNumber("09876365713");
        rizzGallery.setInvitedDate(LocalDateTime.now().toString());
        rizzGallery.setWebsiteAddress("rizzgallery.org");
        rizzGallery.setOrganizationEmployees(employeePeter);
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
        }catch (MeedlException exception){
            log.error("Failed to create user in keycloak", exception);
            log.info("{} {}", exception.getClass().getName(),exception.getMessage());
        }
    }
    @Test
    void createUserWithNullUserIdentity(){
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.createUser(null));
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
    @Order(2)
    void createPassword(){
        try {
            Optional<UserIdentity> existingUser = identityManagementOutputPort.getUserByEmail(john.getEmail());
            assertTrue(existingUser.isPresent());
            assertFalse(existingUser.get().isEnabled());
            assertFalse(existingUser.get().isEmailVerified());

            john.setPassword(password);
            UserIdentity userIdentity = identityManagementOutputPort.createPassword(john.getEmail(), john.getPassword());

            assertNotNull(userIdentity);
            assertNotNull(userIdentity.getId());
            assertTrue(userIdentity.isEmailVerified());
            assertTrue(userIdentity.isEnabled());
            userIdentity.setPassword(john.getPassword());
            enabled = userIdentity.isEnabled();

            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(userIdentity);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }catch (MeedlException e){
            log.info("{} {}",e.getClass().getName(),e.getMessage());
        }
    }
    @Test
    @Order(3)
    void createClient(){
        try {
            OrganizationIdentity organizationIdentity = identityManagementOutputPort.createOrganization(rizzGallery);
            assertNotNull(organizationIdentity);
            assertNotNull(organizationIdentity.getId());
            log.info(organizationIdentity.getId());
            rizzGalleryId = organizationIdentity.getId();
            assertEquals(rizzGallery.getName(), organizationIdentity.getName());
        } catch (MeedlException e) {
            log.error("{}",e.getMessage());
        }
    }
    @Test
    void createClientWithNullOrganizationIdentity(){
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.createOrganization(null));
    }
    @Test
    @Order(4)
    void getClientResource(){
            ClientResource clientResource = identityManagementOutputPort.getClientResource(rizzGalleryId);
            assertNotNull(clientResource);
            ClientRepresentation clientRepresentation = clientResource.toRepresentation();
            log.info("{}", clientRepresentation.getName());
            assertEquals(rizzGallery.getName(), clientRepresentation.getName());
            assertEquals(rizzGallery.getName(), clientRepresentation.getClientId());
            assertEquals(rizzGalleryId, clientRepresentation.getId());

    }

    @Test
    @Order(5)
    void getClientRepresentation(){
        try {
            ClientRepresentation representation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(representation);
            log.info("{}", representation.getId());
            assertEquals(rizzGallery.getName(), representation.getName());
            assertEquals(rizzGallery.getName(), representation.getClientId());
            assertEquals(rizzGalleryId, representation.getId());
        } catch (MeedlException e) {
            log.error("{}",e.getMessage());
        }
    }
    @Test
    @Order(6)
    void deleteClient(){
        try {
            ClientRepresentation clientRepresentation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(clientRepresentation);
            assertEquals(rizzGallery.getName(), clientRepresentation.getName());
            assertEquals(rizzGalleryId, clientRepresentation.getId());
            identityManagementOutputPort.deleteClient(rizzGalleryId);
            assertThrows(MeedlException.class, ()->identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName()));
        } catch (MeedlException e) {
            log.error("{}",e.getMessage());
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
    @ValueSource(strings = {"passwordJ@345    ", "    passwordJ@345"})
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
    @Order(7)
    void login(){
        try {
            john.setPassword(password);
            identityManagementOutputPort.createPassword(john.getEmail(), john.getPassword());
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }catch (MeedlException meedlException){
            log.error("Error logging in user {}", meedlException.getMessage());
        }
    }
    @Test
    void loginWithValidEmailAddressAndInvalidPassword() {
        john.setPassword("invalid-password");
        assertThrows(IdentityException.class, ()->identityManagementOutputPort.login(john));
    }
    @Test
    void loginWithInvalidEmailAndValidPassword() {
        john.setEmail("invalid-email");
        john.setPassword("passwordJ@345");
        assertThrows(IdentityException.class, ()->identityManagementOutputPort.login(john));
    }

    @ParameterizedTest
    @ValueSource(strings = {"    ", StringUtils.SPACE, StringUtils.EMPTY})
    void loginWithNullPassword(String password) {
        john.setPassword(password);
        MeedlException meedlException = assertThrows(MeedlException.class, () ->
                identityManagementOutputPort.login(john));
        assertEquals(EMPTY_INPUT_FIELD_ERROR.getMessage(), meedlException.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This-P@ssw0rd-Is-USed-In-Both-Ch@nge-and-CreatePassword    ",
            "    This-P@ssw0rd-Is-USed-In-Both-Ch@nge-and-CreatePassword",
            "    This-P@ssw0rd-Is-USed-In-Both-Ch@nge-and-CreatePassword    "
    })
    void loginWithValidPasswordWithSpaces(String password) {
        john.setPassword(password);
        try {
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        } catch (MeedlException e) {
            log.error("Failed to login with spaces", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrongpasswordJ@348625"})
    void loginWithInvalidPassword(String password) {
        john.setPassword(password);
        IdentityException exception = assertThrows(IdentityException.class, () -> identityManagementOutputPort.login(john));
        assertEquals(exception.getMessage(), IdentityMessages.INVALID_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    void changePasswordWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.changePassword(null));
    }
    @Test
    void changePasswordWithNullNewPassword() {
        john.setNewPassword(null);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.changePassword(john));
    }
    @ParameterizedTest
    @ValueSource(strings={StringUtils.EMPTY, StringUtils.SPACE, "rniejfkn", "  ADKFDJHFD", "ADKFDJHFD  ", "@ndnue90 -  f"})
    void changePasswordWithInvalidPassword(String password) {
        john.setNewPassword(password);
        Exception exception = assertThrows(MeedlException.class, () -> identityManagementOutputPort.changePassword(john));
        log.info(exception.getMessage());
    }

    @Test
    void logoutWithNull(){
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.logout(null));
    }
    @Test
    void verifyUserExists() {
        UserIdentity userIdentity = null;
        try {
            userIdentity = identityManagementOutputPort.verifyUserExists(john);
        } catch (MeedlException e) {
           log.info("Failed to reset password {}", e.getMessage());
        }
        assertNotNull(userIdentity);
        assertNotNull(userIdentity.getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "dibfjhd"})
    void verifyUserExistsInvalidEmail(String email) {
        john.setEmail(email);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.verifyUserExists(john));
    }
    @Test
    void verifyUserExistsInvalidEmail() {
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.verifyUserExists(null));
    }

    @Test
    @Order(8)
    void changePasswordWithValidPassword() {
        String newPassword = "neWpasswordJ@345";
        AccessTokenResponse accessTokenResponse = null;
        john.setPassword(password);

        log.info(john.getEmail());
        Optional<UserIdentity> existingUser;
        try {
            accessTokenResponse = identityManagementOutputPort.login(john);
            existingUser = identityManagementOutputPort.getUserByEmail(john.getEmail());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(accessTokenResponse);
        assertNotNull(accessTokenResponse.getToken());

        assertTrue(existingUser.isPresent());
        assertNotNull(existingUser.get().getId());

        john.setNewPassword(newPassword);
        john.setId(existingUser.get().getId());
        log.info("user id is {}", john.getId());
        try {
            identityManagementOutputPort.changePassword(john);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

        john.setPassword(newPassword);

        AccessTokenResponse newAccessTokenResponse;
        try {
            newAccessTokenResponse = identityManagementOutputPort.login(john);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(newAccessTokenResponse);
        assertNotNull(newAccessTokenResponse.getToken());

        john.setPassword(password);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.login(john));

    }
    @Test
    @Order(9)
    void enableAccountThatHasBeenEnabled() {
            john.setId(johnId);
            assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
    }
    @Test
    void enableAccountWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(null));
    }

    @Test
    void enableAccountWithNonExistingEmail() {
        john.setEmail("nonexisting@gmail.com");
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ebuefh", " osisiogubjh@mailinator.com"})
    void enableAccountWithInvalidEmail(String email) {
        john.setEmail(email);
        Exception exception = assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableUserAccount(john));
        log.info(exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void reactivateWithOutReason(String reactivateReason) {
        john.setReactivationReason(reactivateReason);
        assertThrows(MeedlException.class,()->identityManagementOutputPort.enableUserAccount(john));

        john.setReactivationReason(null);
        assertThrows(MeedlException.class,()->identityManagementOutputPort.enableUserAccount(john));
    }
    @Test
    @Order(10)
    void disAbleAccount() {
        UserIdentity userIdentity = null;
        try{
            john.setId(johnId);
            john.setEnabled(enabled);
            assertTrue(john.isEnabled());
            userIdentity = identityManagementOutputPort.disableUserAccount(john);
            assertFalse(userIdentity.isEnabled());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }
    }
    @Test
    @Order(11)
    void disAbleAccountAlreadyDisabled() {
          assertThrows(MeedlException.class, ()-> identityManagementOutputPort.disableUserAccount(john));

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
//            assertEquals(2, userRepresentations.size());
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

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deactivateWithInvalidReason(String deactivateReason) {
        john.setReactivationReason(deactivateReason);
        assertThrows(MeedlException.class,()->identityManagementOutputPort.disableUserAccount(john));

        john.setReactivationReason(null);
        assertThrows(MeedlException.class,()->identityManagementOutputPort.disableUserAccount(john));
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
