package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserIdentityServiceTest {
    @Autowired
    private UserIdentityService userIdentityService;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private TokenGeneratorOutputPort tokenGeneratorOutputPort;
    private UserIdentity favour;
    private String userId;
    private String role;
    private String password;
    private String newPassword;

    @BeforeEach
    void setUp(){
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setEmail("favour@gmail.com");
        favour.setCreatedBy("b04d6687-1a6c-4379-9f19-f5b1dbdd3678");
    }

    @Test
    @Order(1)
    void inviteColleague() {
        try {
            // Ensure the user doesn't exist initially
            assertThrows(MiddlException.class, () -> userIdentityOutputPort.findById(favour.getId()));

            // Invite the colleague (create the user)
            UserIdentity invitedColleague = userIdentityService.inviteColleague(favour);

            // Ensure the user was created and has an ID
            assertNotNull(invitedColleague);
            assertNotNull(invitedColleague.getId());


            // Validate the created user's attributes
            assertEquals(favour.getFirstName(), invitedColleague.getFirstName());
            assertEquals(favour.getRole(), invitedColleague.getRole());

            // Retrieve the invited colleague from the database and verify
            UserIdentity foundInvitedColleague = userIdentityOutputPort.findById(favour.getId());
            assertEquals(foundInvitedColleague.getCreatedBy(), invitedColleague.getCreatedBy());
            assertEquals(favour.getLastName(), foundInvitedColleague.getLastName());

            userId = foundInvitedColleague.getId();
            role = foundInvitedColleague.getRole();
        } catch (MiddlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void inviteColleagueWithInviterIdThatDoesNotExist(){
        favour.setCreatedBy("notexisting");
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithEmptyInviterId(){
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithNullInviterId(){
        favour.setCreatedBy(null);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }
    @Test
    void  inviteColleagueWithNullUserIdentity(){
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(new UserIdentity()));
    }
    @Test
    void  inviteColleagueWithEmptyUserIdentity(){
        favour.setFirstName(StringUtils.EMPTY);
        favour.setLastName(StringUtils.EMPTY);
        favour.setEmail(StringUtils.EMPTY);
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithDifferentDomainEmail(){
        favour.setEmail("differentdomainemail@yahoo.com");
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    @Order(2)
    void createPassword(){
        try {
            assertNull(favour.getPassword());
            favour.setPassword("Passkey90@");
            String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
            assertNotNull(generatedToken);
            userIdentityService.createPassword(generatedToken,favour.getPassword());
            password = favour.getPassword();
        }catch (MiddlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }

    }

    @Test
    void createPasswordLessThanEightLetterWord(){
       try{
           favour.setPassword("Key90@");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }

    @Test
    void createPasswordGreaterThanSixteenLetterWord(){
        try{
            favour.setPassword("passWord12345@3345556677788");
            String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
            assertNotNull(generatedToken);
            assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
        }catch (MiddlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }

    @Test
    void createPasswordWithAllLetters(){
       try{
           favour.setPassword("Kayodebbn");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutCapitalLetters(){
       try{
           favour.setPassword("password@123");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    } @Test
    void createPasswordWithoutSmallLetters(){
       try{
           favour.setPassword("PASSWORD@123");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutNumbers(){
       try{
           favour.setPassword("Password@#$%");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutSpecialCharacters(){
       try{
           favour.setPassword("Password1234");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
    @Test
    void createPasswordWithAllNumbers(){
       try{
           favour.setPassword("99900000001234");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
 @Test
    void createPasswordWithAllSymbols(){
       try{
           favour.setPassword("@#$#$%^&&&");
           String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MiddlException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
       }catch (MiddlException middlException){
           log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
       }
    }
     @Test
    void createPasswordWithWrongToken(){
           favour.setPassword("passwoRd@123");
           String generatedToken = "wrong.Token";
           assertNotNull(generatedToken);
           assertThrows(MalformedJwtException.class,()->userIdentityService.createPassword(generatedToken,favour.getPassword()));
    }
    @Test
    @Order(3)
    void login(){
        try {
            assertThrows(MiddlException.class,()->userIdentityService.login(favour));
            favour.setPassword(password);
            userIdentityService.login(favour);
        }catch (MiddlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }

    @Test
    void loginWithInvalidPassword(){
       favour.setPassword("Invalid@456");
       assertThrows(MiddlException.class,()->userIdentityService.login(favour));
    }

    @Test
    @Order(4)
    void changePassword() {
        try {
            favour.setPassword(password);
            userIdentityService.login(favour);

            favour.setId(userId);
            favour.setRole(role);

            favour.setNewPassword("newPassword@8");
            userIdentityService.changePassword(favour);

            newPassword = favour.getPassword();

            // Verify that the password has been updated
            assertEquals(favour.getNewPassword(), favour.getPassword(), "Password should be updated to the new password");


            // Re-login to verify the new password works
            userIdentityService.login(favour);

        } catch (MiddlException middlException) {
            log.info("Exception occurred: {} {}", middlException.getClass().getName(), middlException.getMessage());
        }
    }

    @Test
    @Order(5)
    void changePasswordWithLastPassword() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);
            favour.setId(userId);
            favour.setRole(role);
            favour.setNewPassword(newPassword);
            assertThrows(MiddlException.class,()-> userIdentityService.changePassword(favour));
        } catch (MiddlException middlException) {
            log.info("Exception occurred: {} {}", middlException.getClass().getName(), middlException.getMessage());
        }
    }

    @Test
    @Order(6)
    void changePasswordWithLastTwoPassword() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);
            favour.setId(userId);
            favour.setRole(role);
            favour.setNewPassword(password);
            assertThrows(MiddlException.class,()-> userIdentityService.changePassword(favour));
        } catch (MiddlException middlException) {
            log.info("Exception occurred: {} {}", middlException.getClass().getName(), middlException.getMessage());
        }
    }

    @Test
    @Order(7)
    void resetPassword() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);

            favour.setId(userId);
            favour.setRole(role);


            favour.setPassword("Reset@123");
            userIdentityService.resetPassword(favour.getEmail(),favour.getPassword());
            assertNotEquals(password,favour.getPassword());

            userIdentityService.login(favour);


        } catch (MiddlException middlException) {
            log.info("Exception occurred: {} {}", middlException.getClass().getName(), middlException.getMessage());
        }
    }



    @Test
    @Order(8)
    void resetPasswordWithInvalidEmail() {
        try {
            favour.setPassword(newPassword);
            userIdentityService.login(favour);

            favour.setId(userId);
            favour.setRole(role);

            favour.setPassword("Reset@123");
            favour.setEmail("Invalid@gmail.com");
            assertThrows(MiddlException.class,()->userIdentityService.resetPassword(favour.getEmail(),favour.getPassword()));

        } catch (MiddlException middlException) {
            log.info("Exception occurred: {} {}", middlException.getClass().getName(), middlException.getMessage());
        }
    }

    @Test
    void enableAccountThatHasBeenEnabled() {
       assertThrows(MiddlException.class, () -> userIdentityService.enableAccount(favour));
        }









}