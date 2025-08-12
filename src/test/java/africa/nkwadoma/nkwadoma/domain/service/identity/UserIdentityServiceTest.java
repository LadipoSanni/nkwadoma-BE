package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes.TokenUtils;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email.EmailTokenManager;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.representations.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserIdentityServiceTest {
    @InjectMocks
    private UserIdentityService userIdentityService;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityVerificationOutputPort identityVerificationOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private IdentityManagerOutputPort identityManagerOutPutPort;
    @Mock
    private SendColleagueEmailUseCase sendColleagueEmailUseCase;
    @Mock
    private OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    @Mock
    private AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    @Mock
    private TokenUtils tokenUtils;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private BlackListedTokenAdapter blackListedTokenAdapter;
    private UserIdentity favour;
    @Mock
    private EmailTokenManager emailTokenManager;

//    private String password;
    private String newPassword;
    private final String generatedToken = "generatedToken";
    private OrganizationEmployeeIdentity employeeIdentity;
    private  OrganizationIdentity organizationIdentity;
    @BeforeEach
    void setUp(){
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setPassword("Passkey90@");
        favour.setEmail("favour@gmail.com");
        favour.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setId("c508e3bb-1193-4fc7-aa75-e1335c78ef1e");
        favour.setReactivationReason("Reason for reactivation is to test");
        favour.setDeactivationReason("Reason for deactivation is to test");

        employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setId("1234");
        employeeIdentity.setMeedlUser(favour);

        organizationIdentity = TestData.createOrganizationTestData("OrganizationTest","RC3234322", List.of(employeeIdentity));

    }

    @Test
    @Order(2)
    void createPassword(){
        String password = "Passkey90@";
        try {
            favour.setPassword(password);
            favour.setEmail(favour.getEmail());
            assertNotNull(generatedToken);
            when(tokenUtils.decryptAES(eq(password), any())).thenReturn(password);
            when(emailTokenManager.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            when(identityManagerOutPutPort.createPassword(any())).thenReturn(favour);
            when(userIdentityOutputPort.findByEmail(favour.getEmail())).thenReturn(favour);
            userIdentityService.createPassword(generatedToken,favour.getPassword());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }
    }
    @Test
    @Order(2)
    void viewUserDetail(){
        try {
            favour.setId("c508e3bb-1193-4fc7-aa75-e1335c78ef1e");
            when(userIdentityOutputPort.findById(favour.getId())).thenReturn(favour);
            UserIdentity userIdentity = userIdentityService.viewUserDetail(favour);
            assertNotNull(userIdentity);
            assertEquals(userIdentity.getFirstName(), favour.getFirstName());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }
    }

    @Test
    void login(){
        try {
            userIdentityService.login(favour);
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void loginWithInvalidPassword(){
        try {
            favour.setPassword("Invalid@456");
            when(tokenUtils.decryptAES(eq(favour.getPassword()), any())).thenReturn("Invalid@456");
            doThrow(MeedlException.class).when(identityManagerOutPutPort).login(favour);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class,()-> userIdentityService.login(favour));
    }

    @Test
    void loginWithNullPassword(){
        favour.setPassword(null);
        assertThrows(MeedlException.class,()-> userIdentityService.login(favour));
    }

    @Test
    void loginWithEmptyPassword(){
        favour.setPassword(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> userIdentityService.login(favour));
    }

    @Test
    void refreshToken(){
        try {
            AccessTokenResponse accessTokenResponse = userIdentityService.refreshToken(favour);
            assertNotNull(accessTokenResponse);
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void refreshTokenWithInvalidRefreshToken(){
        favour.setPassword("InvalidRefreshToken");
        assertThrows(MeedlException.class,()-> userIdentityService.refreshToken(favour));
    }

    @Test
    void createPasswordLessThanEightLetterWord(){
           favour.setPassword("Key90@");
        assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }

    @Test
    void createPasswordGreaterThanSixteenLetterWord(){
            favour.setPassword("passWord12345@3345556677788");
            assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }

    @Test
    void createPasswordWithAllLetters(){
           favour.setPassword("Kayodebbn");
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }
    @Test
    void createPasswordWithoutCapitalLetters(){
           favour.setPassword("password@123");
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }
    @Test
    void createPasswordWithoutSmallLetters(){
           favour.setPassword("PASSWORD@123");
        assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }
    @Test
    void createPasswordWithoutNumbers(){
           favour.setPassword("Password@#$%");
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }
    @Test
    void createPasswordWithoutSpecialCharacters(){
           favour.setPassword("Password1234");
//           when(blackListedTokenAdapter.isPresent(generatedToken)).thenReturn(Boolean.FALSE);
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }
    @Test
    void createPasswordWithAllNumbers(){
           favour.setPassword("99900000001234");
//           when(blackListedTokenAdapter.isPresent(generatedToken)).thenReturn(Boolean.FALSE);
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }
 @Test
    void createPasswordWithAllSymbols(){
           favour.setPassword("@#$#$%^&&&");
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }
     @Test
    void createPasswordWithWrongToken(){
        favour.setPassword("passwoRd@123");
       assertThrows(MeedlException.class,()-> userIdentityService.createPassword(null,favour.getPassword()));
    }

    @Test
    void createPasswordWithEmptyToken(){
        favour.setPassword("passwoRd@123");
        String generatedToken = StringUtils.EMPTY;
        assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }
    @Test
    void createPasswordWithNullToken(){
        favour.setPassword("passwoRd@123");
        String generatedToken =null;
        assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }

    @Test
    void createPasswordWithNullPassword(){
            favour.setPassword(null);
            assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }

    @Test
    void createPasswordWithEmptyPassword(){
        try {
            favour.setPassword(StringUtils.EMPTY);
            String generatedToken = emailTokenManager.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void createPasswordAgain(){
        try {
            favour.setPassword("passwoRd@123");
            String generatedToken = emailTokenManager.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void changePassword() {
        try {
            userIdentityService.login(favour);

            favour.setNewPassword("newPassword@8");
            userIdentityService.changePassword(favour);
            favour.setPassword(favour.getNewPassword());
            userIdentityService.login(favour);

        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }
    @Test
    void changePasswordWithNull() {
        assertThrows(MeedlException.class, ()-> userIdentityService.changePassword(null));
    }

    @Test
    void changePasswordWithLastPassword() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);
            favour.setNewPassword(newPassword);
            assertThrows(MeedlException.class,()-> userIdentityService.changePassword(favour));
        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void changePasswordWithLastTwoPassword() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);
            assertThrows(MeedlException.class,()-> userIdentityService.changePassword(favour));
        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }
    @Test
    void resetPassword(){
        String password = "Passkey90@";
        try {
            assertNotNull(generatedToken);
            when(emailTokenManager.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            when(tokenUtils.decryptAES(eq(password), eq("Password provided is not valid. Contact admin.")))
                    .thenReturn(password);
            doNothing().when(identityManagerOutPutPort).resetPassword(any());
            favour.setEnabled(Boolean.TRUE);
            favour.setEmailVerified(Boolean.TRUE);
            when(userIdentityOutputPort.findByEmail(favour.getEmail())).thenReturn(favour);
            userIdentityService.resetPassword(generatedToken, password);
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }
    }

    @Test
    void resetPasswordWithInvalidPassword() {
        assertThrows(MeedlException.class, () -> userIdentityService.resetPassword("invlidToken", "Pasord"));
    }
    @Test
    void resetPasswordForNoneExistingUser() {
        String password = "Pasord*HFNure9";
        try {
            when(tokenUtils.decryptAES(eq(password), any())).thenReturn("Pasord*HFNure9");
            doThrow(MeedlException.class).when(userIdentityOutputPort).findByEmail(any());
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class, () -> userIdentityService.resetPassword("invlidToken", password));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "iurei"})
    void forgotPasswordWithInvalidEmail(String email) {
        assertThrows(MeedlException.class, ()-> userIdentityService.forgotPassword(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void reactivateWithOutReason(String reactivateReason) {
        favour.setReactivationReason(reactivateReason);
        assertThrows(MeedlException.class,()-> userIdentityService.reactivateUserAccount(favour));

        favour.setReactivationReason(null);
        assertThrows(MeedlException.class,()-> userIdentityService.reactivateUserAccount(favour));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deactivateWithOutReason(String deactivateReason) {
        favour.setDeactivationReason(deactivateReason);
        try {
            when(userIdentityService.deactivateUserAccount(favour)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class,()-> userIdentityService.deactivateUserAccount(favour));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewUserDetailWithInvalidId(String userId) {
        favour.setId(userId);
        try {
            when(userIdentityService.viewUserDetail(favour)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class,()-> userIdentityService.viewUserDetail(favour));
    }
    @Test
    void deactivateUserAccountWithNull(){
        assertThrows(MeedlException.class,()-> userIdentityService.deactivateUserAccount(null));
    }
    @Test
    void reactivateUserAccountWithNull(){
        assertThrows(MeedlException.class,()-> userIdentityService.reactivateUserAccount(null));
    }
    @Test
    void reactivateAccountThatHasBeenEnabled() {
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(favour);
            when(identityManagerOutPutPort.enableUserAccount(favour)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, () -> userIdentityService.reactivateUserAccount(favour));
        } catch (MeedlException e) {
            log.error("error occurred {}", e.getMessage());
        }
    }






    @Test
    void shouldThrowExceptionWhenUserTriesToDeactivateSelf() throws MeedlException {
        favour.setCreatedBy(favour.getId());
        when(userIdentityOutputPort.findById(favour.getCreatedBy())).thenReturn(favour);

        MeedlException exception = assertThrows(MeedlException.class, () ->
                userIdentityService.checkIfUserAllowedForAccountActivationActivity(favour, favour, ActivationStatus.DEACTIVATED)
        );

        assertEquals("You are not allowed to deactivate yourself.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTargetUserIsNotAnEmployee() throws MeedlException {
        UserIdentity actor = new UserIdentity();
        actor.setId("actor-id");
        actor.setEmail("admin@org.com");
        actor.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setCreatedBy(actor.getId());

        when(userIdentityOutputPort.findById(actor.getId())).thenReturn(actor);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(favour.getId())).thenReturn(Optional.empty());

        MeedlException exception = assertThrows(MeedlException.class, () ->
                userIdentityService.checkIfUserAllowedForAccountActivationActivity(favour, favour, ActivationStatus.DEACTIVATED)
        );

        assertEquals("You cannot deactivate this user, please contact Meedl admin!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserRoleNotInAllowedRoles() throws MeedlException {
        UserIdentity actor = new UserIdentity();
        actor.setId("actor-id");
        actor.setEmail("admin@org.com");
        actor.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setCreatedBy(actor.getId());
        favour.setRole(IdentityRole.FINANCIER);

        OrganizationEmployeeIdentity emp = new OrganizationEmployeeIdentity();
        emp.setId("emp-id");
        emp.setMeedlUser(favour);
        emp.setOrganization("org-A");

        OrganizationIdentity org = OrganizationIdentity.builder().id("org-A").build();

        when(userIdentityOutputPort.findById(actor.getId())).thenReturn(actor);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(favour.getId())).thenReturn(Optional.of(emp));
        when(organizationIdentityOutputPort.findByEmail("org-A")).thenReturn(org);

        MeedlException exception = assertThrows(MeedlException.class, () ->
                userIdentityService.checkIfUserAllowedForAccountActivationActivity(favour, favour, ActivationStatus.DEACTIVATED)
        );

        assertEquals("You are not authorized to deactivate this user", exception.getMessage());
    }

    @Test
    void shouldAllowOrgAdminToDeactivateOrgAssociate() throws MeedlException {
        UserIdentity actor = new UserIdentity();
        actor.setId("actor-id");
        actor.setEmail("admin@org.com");
        actor.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setCreatedBy(actor.getId());
        favour.setRole(IdentityRole.ORGANIZATION_ASSOCIATE);

        OrganizationEmployeeIdentity emp = new OrganizationEmployeeIdentity();
        emp.setId("emp-id");
        emp.setMeedlUser(favour);
        emp.setOrganization("org-A");

        OrganizationIdentity org = OrganizationIdentity.builder().id("org-A").build();

        when(userIdentityOutputPort.findById(actor.getId())).thenReturn(actor);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(favour.getId())).thenReturn(Optional.of(emp));
        when(organizationIdentityOutputPort.findByEmail("org-A")).thenReturn(org);

        assertDoesNotThrow(() ->
                userIdentityService.checkIfUserAllowedForAccountActivationActivity(favour, favour, ActivationStatus.DEACTIVATED)
        );

    }





}