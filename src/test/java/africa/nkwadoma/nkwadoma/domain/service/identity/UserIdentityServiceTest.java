package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    @Mock
    private TokenUtils tokenUtils;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private UserIdentity favour;
//    private String password;
    private String newPassword;
    private final String generatedToken = "generatedToken";
    private OrganizationEmployeeIdentity employeeIdentity;

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
        favour.setDeactivationReason("Reason for deactivation is to test");;

        employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setId("1234");
        employeeIdentity.setMeedlUser(favour);

    }

    @Test
    void inviteColleague() {
        try {
            when(userIdentityOutputPort.findByEmail(favour.getEmail())).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, () -> userIdentityOutputPort.findByEmail(favour.getEmail()));
            favour.setCreatedBy("83f744df-78a2-4db6-bb04-b81545e78e49");
            favour.setPassword(null);

            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(favour.getCreatedBy())).thenReturn(employeeIdentity);
            when(identityManagerOutPutPort.createUser(any())).thenReturn(favour);
            when(userIdentityOutputPort.save(any())).thenReturn(favour);
            employeeIdentity.setId(favour.getId());
            when(organizationEmployeeIdentityOutputPort.save(any())).thenReturn(employeeIdentity);
            when(organizationIdentityOutputPort.findById(any())).thenReturn(new OrganizationIdentity());
//            doNothing().when(sendColleagueEmailUseCase).sendColleagueEmail("",favour);

            UserIdentity invitedColleague = userIdentityService.inviteColleague(favour);
            log.info("invited colleague {}", invitedColleague.getId());
            assertNotNull(invitedColleague);
            assertNotNull(invitedColleague.getId());

            assertEquals(favour.getFirstName(), invitedColleague.getFirstName());
            assertEquals(favour.getRole(), invitedColleague.getRole());

            verify(organizationEmployeeIdentityOutputPort, times(1)).findByEmployeeId(favour.getCreatedBy());
            verify(identityManagerOutPutPort, times(1)).createUser(favour);
            verify(userIdentityOutputPort, times(1)).save(favour);

        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void inviteColleagueWithInviterIdThatDoesNotExist(){
        try {
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(any())).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        favour.setCreatedBy("83f744df-78a2-4db6-bb04-b81545e78e49");
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(favour));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void inviteColleagueWithEmptyInviterId(String value){
        favour.setCreatedBy(value);
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithNullInviterId(){
        favour.setCreatedBy(null);
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(favour));
    }
    @Test
    void  inviteColleagueWithNullUserIdentity(){
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(null));
    }
    @Test
    void  inviteColleagueWithEmptyUserIdentity(){
        favour.setFirstName(StringUtils.EMPTY);
        favour.setLastName(StringUtils.EMPTY);
        favour.setEmail(StringUtils.EMPTY);
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithDifferentDomainEmail(){
        try {
            favour.setEmail("favour@gmail.com");
            employeeIdentity.setMeedlUser(favour);
            when(userIdentityService.inviteColleague(favour)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }

        favour.setEmail("differentdomainemail@yahoo.com");
        assertThrows(MeedlException.class,()-> userIdentityService.inviteColleague(favour));
    }

    @Test
    @Order(2)
    void createPassword(){
        try {
            favour.setPassword("Passkey90@");
            assertNotNull(generatedToken);
            when(tokenUtils.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            when(identityManagerOutPutPort.createPassword(favour.getEmail(), favour.getPassword())).thenReturn(favour);
            when(userIdentityOutputPort.findByEmail(favour.getEmail())).thenReturn(favour);
            userIdentityService.createPassword(generatedToken,favour.getPassword());
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
        favour.setPassword("Invalid@456");
        try {
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
           assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));

    }
    @Test
    void createPasswordWithAllNumbers(){
           favour.setPassword("99900000001234");
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
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()-> userIdentityService.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void createPasswordAgain(){
        try {
            favour.setPassword("passwoRd@123");
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
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
            newPassword = favour.getPassword();

            assertEquals(favour.getNewPassword(), favour.getPassword(), "Password should be updated to the new password");

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
        try {
            assertNotNull(generatedToken);
            when(tokenUtils.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            doNothing().when(identityManagerOutPutPort).resetPassword(any());
            favour.setEnabled(Boolean.TRUE);
            favour.setEmailVerified(Boolean.TRUE);
            when(userIdentityOutputPort.findByEmail(favour.getEmail())).thenReturn(favour);
            userIdentityService.resetPassword(generatedToken,"Passkey90@");
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
        try {
            doThrow(MeedlException.class).when(userIdentityOutputPort).findByEmail(any());
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class, () -> userIdentityService.resetPassword("invlidToken", "Pasord*HFNure9"));
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
    void deactivateAccountAlreadyDisabled() {
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(favour);
            when(identityManagerOutPutPort.disableUserAccount(favour)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, ()-> userIdentityService.deactivateUserAccount(favour));
        } catch (MeedlException e) {
            log.error("error occured {}", e.getMessage());
        }

    }













}