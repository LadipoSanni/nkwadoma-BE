package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserIdentityServiceTest {
    @Autowired
    private UserIdentityService userIdentityService;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private TokenGeneratorOutputPort tokenGeneratorOutputPort;
    private UserIdentity favour;

    @BeforeEach
    void setUp(){
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setEmail("favour@gmail.com");
        favour.setCreatedBy("5b44bc18-ee08-4559-94d3-e8f7fec4a6fc");
    }

    @Test
    void inviteColleague(){
       try{
          assertThrows(MiddlException.class,()->userIdentityOutputPort.findById(favour.getId()));
           UserIdentity invitedColleague = userIdentityService.inviteColleague(favour);
           assertNotNull(invitedColleague);
           assertEquals(favour.getFirstName(),invitedColleague.getFirstName());
           assertEquals(favour.getRole(),invitedColleague.getRole());
           UserIdentity foundInvitedColleague = userIdentityOutputPort.findById(favour.getId());
           assertEquals(foundInvitedColleague.getCreatedBy(),invitedColleague.getCreatedBy());
           assertEquals(favour.getLastName(),foundInvitedColleague.getLastName());
       }catch (MiddlException exception){
           log.info("{} {}",exception.getClass().getName(),exception.getMessage());
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
    void createPassword(){
        try {
            assertNull(favour.getPassword());
            favour.setPassword("Passkey90@");
            String generatedToken = tokenGeneratorOutputPort.generateToken(favour.getEmail());
            assertNotNull(generatedToken);
            log.info("{}",favour.getPassword());
            userIdentityService.createPassword(generatedToken,favour.getPassword());
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




}