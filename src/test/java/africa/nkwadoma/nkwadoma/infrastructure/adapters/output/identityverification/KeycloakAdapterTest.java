package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
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

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class KeycloakAdapterTest {
    @Autowired
    private IdentityManagerOutputPort identityManagementOutputPort;
    private UserIdentity john;
    private UserIdentity peter;
    private String johnId;
    private boolean enabled;
    private final String password = "P@ssw0rd-4-Test";
    private final String newPassword = "neWpasswordJ@345";
    private OrganizationIdentity rizzGallery;
    private String rizzGalleryId ;
    private String johnEmail;

    @BeforeEach
    void setUp() {
        john = TestData.createTestUserIdentity("johnmax@lendspace.com");
        peter = TestData.createTestUserIdentity("peter@lendspace.com");
        johnEmail = "johnmax@lendspace.com";
        OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setMeedlUser(peter);

        List<OrganizationEmployeeIdentity> employeePeter = new ArrayList<>();
        employeePeter.add(employeeIdentity);

        rizzGallery = TestData.createOrganizationTestData("Rizz Gallery 1' alone", "RC8789945",employeePeter);
    }

    @Test
    @Order(1)
    void createUser() {
        try {
            UserIdentity createdUser = identityManagementOutputPort.createUser(john);
            assertNotNull(createdUser);
            assertNotNull(createdUser.getId());
            assertEquals(john.getId(), createdUser.getId());
            johnId = createdUser.getId();
            assertEquals(createdUser.getEmail(), john.getEmail());
            assertEquals(createdUser.getFirstName(), john.getFirstName());
            assertEquals(createdUser.getLastName(), john.getLastName());
        }catch (MeedlException exception){
            log.error("Failed to create user in keycloak", exception);
            log.info("{} {}", exception.getClass().getName(),exception.getMessage());
        }
    }
    @Test
    void updateUserEmail(){
        peter.setEmail("updatedtestemail@emial.com");
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.updateUserData(peter));
    }
    @Test
    void updateUserDataWithNull(){
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.updateUserData(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid-uuid"})
    void updateUserDataWithInvalidId(String id){
        peter.setId(id);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.updateUserData(peter));
    }
    @Test
    void updateUser() {
        try {
            String firstName = peter.getFirstName();
            String lastName = peter.getLastName();
            UserIdentity createdUser = identityManagementOutputPort.createUser(peter);
            assertNotNull(createdUser);
            assertNotNull(createdUser.getId());
            assertEquals(peter.getFirstName(), createdUser.getFirstName());
            assertEquals(peter.getLastName(), createdUser.getLastName());
            assertEquals(peter.getRole(), createdUser.getRole());
            assertEquals(peter.getEmail(), createdUser.getEmail());
            peter.setFirstName("UpdatedPeter");
            peter.setLastName("UpdatedLastName");
            peter.setId(createdUser.getId());
            UserIdentity updateUserData = identityManagementOutputPort.updateUserData(peter);
            assertNotEquals(firstName, updateUserData.getFirstName());
            assertNotEquals(lastName, updateUserData.getLastName());
            assertEquals(peter.getFirstName(), updateUserData.getFirstName());
            assertEquals(peter.getLastName(), updateUserData.getLastName());
            assertEquals(peter.getEmail(), updateUserData.getEmail());

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
            Optional<UserIdentity> existingUser = identityManagementOutputPort.getUserByEmail(johnEmail);
            assertTrue(existingUser.isPresent());
            assertFalse(existingUser.get().isEnabled());
            assertFalse(existingUser.get().isEmailVerified());

            john.setPassword(password);
            john.setEmail(john.getEmail());
            UserIdentity userIdentity = identityManagementOutputPort.createPassword(john);

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
            log.error("{} {}",e.getClass().getName(),e.getMessage());
        }
    }
    @Test
    @Order(3)
    void createClient(){
        try {
            OrganizationIdentity organizationIdentity = identityManagementOutputPort.createKeycloakClient(rizzGallery);
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
        assertThrows(MeedlException.class,()-> identityManagementOutputPort.createKeycloakClient(null));
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
    void getClientRepresentationById() {
        ClientRepresentation clientRepresentation = null;
        try {
            clientRepresentation = identityManagementOutputPort.getClientRepresentationByClientId(rizzGallery.getName());
        } catch (MeedlException e) {
            log.error("Error getting client representation {}", e.getMessage());
        }
        assertNotNull(clientRepresentation);
        assertNotNull(clientRepresentation.getName());
        log.info("Client representation {}", clientRepresentation.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void getClientRepresentationWithInvalidId(String id) {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.getClientRepresentationByClientId(id));
    }

    @Test
    void getClientRepresentationWithNoneExistingId() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.getClientRepresentationByClientId("none existing id"));
    }
    @Test
    @Order(7)
    void disableClient(){
        try {
            ClientRepresentation representation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(representation);
            assertTrue(representation.isEnabled());
            rizzGallery.setId(rizzGalleryId);
            identityManagementOutputPort.disableClient(rizzGallery);
            representation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(representation);
            assertFalse(representation.isEnabled());
        }catch (MeedlException e){
            log.error("Failed to disable organization identity {} ", e.getMessage());
        }
    }
    @Test
    void disableOrganizationWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.disableClient(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invaliduuid"})
    void disableOrganizationWithInvalidId(String id) {
        OrganizationIdentity megaOrganization = new OrganizationIdentity();
        megaOrganization.setId(id);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.disableClient(megaOrganization));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invaliduuid"})
    void enableOrganizationWithInvalidId(String id) {
        OrganizationIdentity megaOrganization = new OrganizationIdentity();
        megaOrganization.setId(id);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableClient(megaOrganization));
    }
    @Test
    void enableOrganizationWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.enableClient(null));
    }

    @Test
    @Order(8)
    void enableClient(){
        try {
            ClientRepresentation representation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(representation);
            assertFalse(representation.isEnabled());
            rizzGallery.setId(rizzGalleryId);
            identityManagementOutputPort.enableClient(rizzGallery);
            representation = identityManagementOutputPort.getClientRepresentationByName(rizzGallery.getName());
            assertNotNull(representation);
            assertTrue(representation.isEnabled());
        }catch (MeedlException e){
            log.error("Failed to disable organization identity {} ", e.getMessage());
        }
    }

    @Test
    @Order(9)
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

    @Test
    @Order(9)
    void getUserIdentityById(){
        UserIdentity userIdentity = null;
        try {
            userIdentity = identityManagementOutputPort.getUserById(johnId);
        } catch (MeedlException e) {
            log.error("User with this Id : {} , not found ", johnId);
        }
        assertNotNull(userIdentity);
        assertNotNull(userIdentity.getEmail());
        log.info("UserRepresentation : {}", userIdentity.getEmail());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid id"})
    void getUserIdentityByInvalidId(String id){
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.getUserById(id));
    }
    @Test
    void getUserIdentityByValidNoneExistingId() {
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.getUserById("123e4567-e89b-12d3-a456-426614174000\""));
    }
    @ParameterizedTest
    @ValueSource(strings = {"passwordJ@345    ", "    passwordJ@345"})
    void createPasswordWithSpaces(String password) {
        try {
            john.setEmail(john.getEmail());
            UserIdentity userIdentity = identityManagementOutputPort.createPassword(john);
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
    @Order(10)
    void login(){
        try {
            john.setEmail(john.getEmail());
            john.setPassword(password);
            identityManagementOutputPort.createPassword(john);
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }catch (MeedlException meedlException){
            log.error("Error logging in user {}", meedlException.getMessage());
        }
    }

    @Test
    @Order(11)
    void refreshToken(){
        try {
            john.setEmail(john.getEmail());
            john.setPassword(password);
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());

            john.setRefreshToken(accessTokenResponse.getRefreshToken());
            AccessTokenResponse refreshTokenResponse = identityManagementOutputPort.refreshToken(john);

            assertNotNull(refreshTokenResponse);
            assertNotNull(refreshTokenResponse.getToken());
            assertNotNull(refreshTokenResponse.getRefreshToken());
            assertNotEquals(refreshTokenResponse, accessTokenResponse);
            assertNotEquals(refreshTokenResponse.getToken(), accessTokenResponse.getToken());
        } catch (MeedlException meedlException){
            log.error("Error authenticating in user {}", meedlException.getMessage());
        }
    }

    @Test
    void refreshTokenWithInvalidRefreshToken(){
        try {
            john.setEmail(john.getEmail());
            john.setPassword(password);
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }
        catch (MeedlException meedlException){
            log.error("Error authenticating user {}", meedlException.getMessage());
        }
        john.setRefreshToken("invalid-refresh-token");
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.refreshToken(john));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", StringUtils.EMPTY, StringUtils.SPACE})
    void refreshTokenWithEmptyRefreshToken(String refreshToken){
        try {
            john.setEmail(john.getEmail());
            john.setPassword(password);
            AccessTokenResponse accessTokenResponse = identityManagementOutputPort.login(john);
            assertNotNull(accessTokenResponse);
            assertNotNull(accessTokenResponse.getToken());
            assertNotNull(accessTokenResponse.getRefreshToken());
        }
        catch (MeedlException meedlException){
            log.error("Error authenticating user {}", meedlException.getMessage());
        }
        john.setRefreshToken(refreshToken);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.refreshToken(john));
    }

    @Test
    void loginWithValidEmailAddressAndInvalidPassword() {
        john.setPassword("invalid-password");
        assertThrows(MeedlException.class, ()->identityManagementOutputPort.login(john));
    }
    @Test
    void loginWithInvalidEmailAndValidPassword() {
        john.setEmail("invalid-email");
        john.setPassword("passwordJ@345");
        assertThrows(MeedlException.class, ()->identityManagementOutputPort.login(john));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void loginWithNullPassword(String password) {
        john.setPassword(password);
        MeedlException meedlException = assertThrows(MeedlException.class, () ->
                identityManagementOutputPort.login(john));
        assertEquals("Password can not be empty", meedlException.getMessage());
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
    @ValueSource(strings = {"P@ssw0rd!3"})
    void loginWithInvalidPassword(String password) {
        john.setPassword(password);
        IdentityException exception = assertThrows(IdentityException.class, () -> identityManagementOutputPort.login(john));
        assertEquals(exception.getMessage(), IdentityMessages.INVALID_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    void changePasswordWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.setPassword(null));
    }
    @Test
    void changePasswordWithNullNewPassword() {
        john.setNewPassword(null);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.setPassword(john));
    }
    @ParameterizedTest
    @ValueSource(strings={StringUtils.EMPTY, StringUtils.SPACE, "rniejfkn", "  ADKFDJHFD", "ADKFDJHFD  ", "@ndnue90 -  f"})
    void changePasswordWithInvalidPassword(String password) {
        john.setNewPassword(password);
        Exception exception = assertThrows(MeedlException.class, () -> identityManagementOutputPort.setPassword(john));
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
            userIdentity = identityManagementOutputPort.verifyUserExistsAndIsEnabled(john);
        } catch (MeedlException e) {
           log.info("Failed to verifyUser password {}", e.getMessage());
        }
        assertNotNull(userIdentity);
        assertNotNull(userIdentity.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "fndjnke"})
    void verifyUserWithInvalidEmail(String email){
        john.setEmail(email);
        assertThrows(MeedlException.class,()->identityManagementOutputPort.verifyUserExistsAndIsEnabled(john));
    }
    @Test
    void verifyUserWithValidIdNotFound(){
        john.setEmail("validemail@gmail.com");
        assertThrows(MeedlException.class,()->identityManagementOutputPort.verifyUserExistsAndIsEnabled(john));
    }
    @Test
    void resetPasswordWithNull() {
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.resetPassword(null));
    }
    @Test
    void resetPasswordWithNullPassword() {
        john.setEmail(null);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.resetPassword(john));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "njdfkjn"})
    void resetPasswordWithNullEmail(String email) {
        john.setEmail(email);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.resetPassword(john));
    }
    @Test
    void resetPasswordWithNullNewPassword() {
        john.setNewPassword(null);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.resetPassword(john));
    }
    @ParameterizedTest
    @ValueSource(strings={StringUtils.EMPTY, StringUtils.SPACE, "rniejfkn", "  ADKFDJHFD", "ADKFDJHFD  ", "@ndnue90 -  f"})
    void resetPasswordWithInvalidPassword(String password) {
        john.setNewPassword(password);
        assertThrows(MeedlException.class, () -> identityManagementOutputPort.resetPassword(john));
    }

    @Test
    @Order(12)
    void changePasswordWithValidPassword() {
        AccessTokenResponse accessTokenResponse = null;
        john.setPassword(password);

        log.info(john.getEmail());
        Optional<UserIdentity> existingUser = Optional.empty();
        try {
            accessTokenResponse = identityManagementOutputPort.login(john);
            existingUser = identityManagementOutputPort.getUserByEmail(john.getEmail());
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }

        assertNotNull(accessTokenResponse);
        assertNotNull(accessTokenResponse.getToken());

        assertTrue(existingUser.isPresent());
        assertNotNull(existingUser.get().getId());

        john.setNewPassword(newPassword);
        john.setId(existingUser.get().getId());
        log.info("user id is {}", john.getId());
        try {
            identityManagementOutputPort.setPassword(john);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }

        john.setPassword(newPassword);

        AccessTokenResponse newAccessTokenResponse = null;
        try {
            newAccessTokenResponse = identityManagementOutputPort.login(john);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }

        assertNotNull(newAccessTokenResponse);
        assertNotNull(newAccessTokenResponse.getToken());

        john.setPassword(password);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.login(john));

    }

    @Test
    @Order(13)
    void resetPasswordWithValidPassword() {
        AccessTokenResponse accessTokenResponse = null;
        john.setPassword(newPassword);

        log.info(john.getEmail());
        Optional<UserIdentity> existingUser = Optional.empty();
        try {
            accessTokenResponse = identityManagementOutputPort.login(john);
            existingUser = identityManagementOutputPort.getUserByEmail(john.getEmail());
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }

        assertNotNull(accessTokenResponse);
        assertNotNull(accessTokenResponse.getToken());
        assertTrue(existingUser.isPresent());
        assertNotNull(existingUser.get().getId());

        john.setNewPassword(password);
        john.setId(existingUser.get().getId());
        try {
            identityManagementOutputPort.resetPassword(john);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }

        john.setPassword(password);
        AccessTokenResponse newAccessTokenResponse = null;
        try {
            newAccessTokenResponse = identityManagementOutputPort.login(john);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertNotNull(newAccessTokenResponse);
        assertNotNull(newAccessTokenResponse.getToken());

        john.setPassword(newPassword);
        assertThrows(MeedlException.class, ()-> identityManagementOutputPort.login(john));

    }

    @Test
    @Order(14)
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
    @Order(15)
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
    @Order(16)
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
