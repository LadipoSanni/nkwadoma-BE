package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class UserIdentityAdapterTest {
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private UserIdentity john;



    @BeforeEach
    void setUp(){
        john = new UserIdentity();
        john.setFirstName("John");
        john.setLastName("Johnson");
        john.setEmail("john@johnson.com");
        john.setUserId(john.getEmail());
        john.setPhoneNumber("09087655454");
        john.setEmailVerified(true);
        john.setEnabled(true);
        john.setCreatedAt(LocalDateTime.now().toString());
        john.setRole("TRAINEE");
        john.setCreatedBy("Smart");

    }

    @Test
    void saveUser(){
        try{
            assertThrows(IdentityException.class, ()->userIdentityOutputPort.findByEmail(john.getEmail()));
            UserIdentity savedJohn = userIdentityOutputPort.save(john);
            assertNotNull(savedJohn);
            UserIdentity findJohn = userIdentityOutputPort.findByEmail(john.getEmail());
            assertEquals(findJohn.getFirstName(),savedJohn.getFirstName());
            assertEquals(findJohn.getLastName(),savedJohn.getLastName());
            assertEquals(findJohn.getCreatedBy(),savedJohn.getCreatedBy());

        }catch (MiddlException exception){
            log.info("{} {}->",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveUserWithExistingEmail(){
        try{
            UserIdentity savedJohn = userIdentityOutputPort.save(john);
            assertEquals(john.getUserId(),savedJohn.getUserId());
        }catch (MiddlException exception){
            log.info("{} {}->",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveUserWithNullUserIdentity(){
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(null));
    }

    @Test
    void saveUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithInvalidEmail(){
        john.setEmail("invalid");
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullFirstName(){
        john.setFirstName(null);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithEmptyFirstName(){
        john.setFirstName(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithNullLastName(){
        john.setLastName(null);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithEmptyLastName(){
        john.setLastName(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullRole(){
        john.setRole(null);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithEmptyRole(){
        john.setRole(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullCreatedBy(){
        john.setCreatedBy(null);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithEmptyCreatedBy(){
        john.setCreatedBy(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.save(john));
    }


    @Test
    void deleteUser(){
       try{
            UserIdentity existingUser = userIdentityOutputPort.findByEmail(john.getEmail());
            assertEquals(john.getUserId(),existingUser.getUserId());
            userIdentityOutputPort.deleteUserByEmail(john.getEmail());
       }catch (MiddlException exception){
           log.info("{} ->",exception.getMessage());
       }
        assertThrows(IdentityException.class,()-> userIdentityOutputPort.findByEmail(john.getEmail()));
    }


    @Test
    void deleteUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }


    @Test
    void deleteUserWithNonExistingEmail(){
        john.setEmail("invalid@gmail.com");
        assertThrows(IdentityException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithInvalidEmailFormat(){
        john.setEmail("invalid");
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithUserId(){
        try{
            UserIdentity existingUser = userIdentityOutputPort.findById(john.getUserId());
            assertEquals(existingUser.getUserId(),john.getUserId());

            userIdentityOutputPort.deleteUserById(john.getUserId());
        }catch (MiddlException exception){
            log.info("{} ->",exception.getMessage());
        }
        assertThrows(IdentityException.class,()-> userIdentityOutputPort.findById(john.getUserId()));
    }


    @Test
    void deleteUserWithEmptyId(){
        john.setUserId(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserById(john.getUserId()));
    }
    @Test
    void deleteUserWithNullId(){
        john.setUserId(null);
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserById(john.getUserId()));
    }

    @Test
    void deleteUserWithNonExistingUserId(){
        john.setUserId("notvalid@gmail.com");
        assertThrows(MiddlException.class,()->userIdentityOutputPort.deleteUserById(john.getUserId()));
    }

    @Test
    void findUserByEmail(){
        try {
            UserIdentity existingUser = userIdentityOutputPort.findById(john.getUserId());
            assertEquals(existingUser.getUserId(),john.getUserId());
            UserIdentity userIdentity =  userIdentityOutputPort.findByEmail(john.getEmail());
            assertNotNull(userIdentity);
        } catch (MiddlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
    }

    @Test
    void findUserByNonExistingEmail(){
        john.setEmail("doesnotexist@gmail.com");
        assertThrows(IdentityException.class, ()->userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void findUserWithAnInvalidEmailFormat(){
        john.setEmail("invalid");
        assertThrows(MiddlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  findUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(MiddlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  findUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(MiddlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  updateUser(){
        john.setFirstName("Sharon");
        john.setLastName("lastname");
    }


    @AfterAll
    void cleanUp(){
        try {
            userIdentityOutputPort.deleteUserByEmail(john.getEmail());
        }catch (MiddlException middlException){
            log.info("{} {}",middlException.getClass().getName(),middlException.getMessage());
        }
    }


}