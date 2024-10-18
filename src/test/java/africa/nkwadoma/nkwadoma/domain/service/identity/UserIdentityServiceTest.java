package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
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
    private CreateUserUseCase createUserUseCase;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private TokenUtils tokenUtils;
    private UserIdentity favour;
    private String userId;
    private IdentityRole role;
    private String password;
    private String newPassword;

    @BeforeEach
    void setUp(){
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setEmail("favour@gmail.com");
        favour.setRole(IdentityRole.INSTITUTE_ADMIN);
        favour.setCreatedBy("c508e3bb-1193-4fc7-aa75-e1335c78ef1e");
    }

    @Test
    @Order(1)
    void inviteColleague() {
        try {
            // Ensure the user doesn't exist initially
            assertThrows(MeedlException.class, () -> userIdentityOutputPort.findById(favour.getId()));

            // Invite the colleague (create the user)
            UserIdentity invitedColleague = createUserUseCase.inviteColleague(favour);

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
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void inviteColleagueWithInviterIdThatDoesNotExist(){
        favour.setCreatedBy("notexisting");
        assertThrows(MeedlException.class,()->createUserUseCase.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithEmptyInviterId(){
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> createUserUseCase.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithNullInviterId(){
        favour.setCreatedBy(null);
        assertThrows(MeedlException.class,()-> createUserUseCase.inviteColleague(favour));
    }
    @Test
    void  inviteColleagueWithNullUserIdentity(){
        assertThrows(MeedlException.class,()-> createUserUseCase.inviteColleague(null));
    }
    @Test
    void  inviteColleagueWithEmptyUserIdentity(){
        favour.setFirstName(StringUtils.EMPTY);
        favour.setLastName(StringUtils.EMPTY);
        favour.setEmail(StringUtils.EMPTY);
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> createUserUseCase.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithDifferentDomainEmail(){
        favour.setEmail("differentdomainemail@yahoo.com");
        assertThrows(MeedlException.class,()-> createUserUseCase.inviteColleague(favour));
    }

    @Test
    @Order(2)
    void createPassword(){
        try {
            assertNull(favour.getPassword());
            favour.setPassword("Passkey90@");
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertNotNull(generatedToken);
            createUserUseCase.createPassword(generatedToken,favour.getPassword());
            password = favour.getPassword();
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());
        }

    }

    @Test
    void createPasswordLessThanEightLetterWord(){
       try{
           favour.setPassword("Key90@");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()-> createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}",meedlException.getClass().getName(),meedlException.getMessage());
       }
    }

    @Test
    void createPasswordGreaterThanSixteenLetterWord(){
        try{
            favour.setPassword("passWord12345@3345556677788");
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertNotNull(generatedToken);
            assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }

    @Test
    void createPasswordWithAllLetters(){
       try{
           favour.setPassword("Kayodebbn");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutCapitalLetters(){
       try{
           favour.setPassword("password@123");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    } @Test
    void createPasswordWithoutSmallLetters(){
       try{
           favour.setPassword("PASSWORD@123");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()-> createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutNumbers(){
       try{
           favour.setPassword("Password@#$%");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
    @Test
    void createPasswordWithoutSpecialCharacters(){
       try{
           favour.setPassword("Password1234");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
    @Test
    void createPasswordWithAllNumbers(){
       try{
           favour.setPassword("99900000001234");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
 @Test
    void createPasswordWithAllSymbols(){
       try{
           favour.setPassword("@#$#$%^&&&");
           String generatedToken = tokenUtils.generateToken(favour.getEmail());
           assertNotNull(generatedToken);
           assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
       }catch (MeedlException meedlException){
           log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
       }
    }
     @Test
    void createPasswordWithWrongToken(){
           favour.setPassword("passwoRd@123");
           String generatedToken = "wrong.Token";
           assertNotNull(generatedToken);
           assertThrows(MalformedJwtException.class,()-> createUserUseCase.createPassword(generatedToken,favour.getPassword()));
    }

    @Test
    void createPasswordWithEmptyToken(){
        favour.setPassword("passwoRd@123");
        String generatedToken = StringUtils.EMPTY;
        assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
    }
    @Test
    void createPasswordWithNullToken(){
        favour.setPassword("passwoRd@123");
        String generatedToken =null;
        assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
    }

    @Test
    void createPasswordWithNullPassword(){
        try {
            favour.setPassword(null);
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void createPasswordWithEmptyPassword(){
        try {
            favour.setPassword(StringUtils.EMPTY);
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void createPasswordAgain(){
        try {
            favour.setPassword("passwoRd@123");
            String generatedToken = tokenUtils.generateToken(favour.getEmail());
            assertThrows(MeedlException.class,()->createUserUseCase.createPassword(generatedToken,favour.getPassword()));
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    @Order(3)
    void login(){
        try {
            assertThrows(MeedlException.class,()->createUserUseCase.login(favour));
            favour.setPassword(password);
            createUserUseCase.login(favour);
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void loginWithInvalidPassword(){
       favour.setPassword("Invalid@456");
       assertThrows(MeedlException.class,()->createUserUseCase.login(favour));
    }

    @Test
    void loginWithNullPassword(){
       favour.setPassword(null);
       assertThrows(MeedlException.class,()->createUserUseCase.login(favour));
    }

    @Test
    void loginWithEmptyPassword(){
       favour.setPassword(StringUtils.EMPTY);
       assertThrows(MeedlException.class,()->createUserUseCase.login(favour));
    }



    @Test
    @Order(4)
    void changePassword() {
        try {
            favour.setPassword(password);
            createUserUseCase.login(favour);

            favour.setId(userId);
            favour.setRole(role);

            favour.setNewPassword("newPassword@8");
            createUserUseCase.changePassword(favour);

            newPassword = favour.getPassword();

            // Verify that the password has been updated
            assertEquals(favour.getNewPassword(), favour.getPassword(), "Password should be updated to the new password");


            // Re-login to verify the new password works
            createUserUseCase.login(favour);

        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    @Order(5)
    void changePasswordWithLastPassword() {
        try {
            favour.setPassword(newPassword);
            createUserUseCase.login(favour);
            favour.setId(userId);
            favour.setRole(role);
            favour.setNewPassword(newPassword);
            assertThrows(MeedlException.class,()-> createUserUseCase.changePassword(favour));
        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    @Order(6)
    void changePasswordWithLastTwoPassword() {
        try {
            favour.setPassword(newPassword);
            createUserUseCase.login(favour);
            favour.setId(userId);
            favour.setRole(role);
            favour.setNewPassword(password);
            assertThrows(MeedlException.class,()-> createUserUseCase.changePassword(favour));
        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    @Order(7)
    void resetPassword() {
        try {
            favour.setPassword(newPassword);
            createUserUseCase.login(favour);

            favour.setId(userId);
            favour.setRole(role);


            favour.setPassword("Reset@123");
            createUserUseCase.resetPassword(favour.getEmail(),favour.getPassword());
            assertNotEquals(password,favour.getPassword());

            createUserUseCase.login(favour);


        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }



    @Test
    @Order(8)
    void resetPasswordWithInvalidEmail() {
        try {
            favour.setPassword(newPassword);
            createUserUseCase.login(favour);

            favour.setId(userId);
            favour.setRole(role);

            favour.setPassword("Reset@123");
            favour.setEmail("Invalid@gmail.com");
            assertThrows(MeedlException.class,()->createUserUseCase.resetPassword(favour.getEmail(),favour.getPassword()));

        } catch (MeedlException meedlException) {
            log.info("Exception occurred: {} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }

    @Test
    void enableAccountThatHasBeenEnabled() {
       assertThrows(MeedlException.class, () -> createUserUseCase.enableAccount(favour));
        }

    @Test
     void forgotPassword() {
         try {
             UserIdentity userIdentity =  createUserUseCase.forgotPassword(favour.getEmail());
             assertNotNull(userIdentity);
             assertEquals(favour.getFirstName(),userIdentity.getFirstName());

         }catch (MeedlException meedlException){
             log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
         }
    }

    @Test
     void forgotPasswordWithInvalidEmailAAddress() {
        favour.setEmail("wrongemail@gmail.com");
        assertThrows(MeedlException.class, () -> createUserUseCase.forgotPassword(favour.getEmail()));
    }

    @Test
    void forgotPasswordWithNullEmailAAddress() {
        favour.setEmail(null);
        assertThrows(MeedlException.class, () -> createUserUseCase.forgotPassword(favour.getEmail()));
    }
    @Test
    void checkLastFivePassword() throws MeedlException {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEmail("johnmax@lendspace.com");
        userIdentity.setId("8ba30e52-60d2-4fd7-ac7b-a558b1d20c9d");

        createUserUseCase.checkNewPasswordMatchLastFive(userIdentity);
    }

    @Test
    void forgotPasswordWithEmptyEmailAddress() {
        favour.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> createUserUseCase.forgotPassword(favour.getEmail()));
    }













}