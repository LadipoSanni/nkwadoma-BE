package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole.LOANEE;
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
        john = TestData.createTestUserIdentity("john2@johnson.com", "ead0f7cb-5483-4bc8-b271-513970a9c363");

    }

    @Test
    @Order(1)
    void saveUser(){
        try{
            assertThrows(MeedlException.class, ()->userIdentityOutputPort.findByEmail(john.getEmail()));
            UserIdentity savedJohn = userIdentityOutputPort.save(john);
            assertNotNull(savedJohn);
            UserIdentity findJohn = userIdentityOutputPort.findByEmail(john.getEmail());
            assertEquals(findJohn.getFirstName(),savedJohn.getFirstName());
            assertEquals(findJohn.getLastName(),savedJohn.getLastName());
            assertEquals(findJohn.getCreatedBy(),savedJohn.getCreatedBy());
        }catch (MeedlException exception){
            log.error("{} {}->",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void findAllUserWithNullRole(){
        assertThrows(MeedlException.class, ()-> userIdentityOutputPort.findAllByRole(null));
    }

    @Test
    @Order(2)
    void saveUserWithExistingEmail() {
        john.setId(null);
        assertThrows(MeedlException.class, () -> userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullUserIdentity(){
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(null));
    }

    @Test
    void saveUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithInvalidEmail(){
        john.setEmail("invalid");
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullFirstName(){
        john.setFirstName(null);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithEmptyFirstName(){
        john.setFirstName(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithNullLastName(){
        john.setLastName(null);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    void saveUserWithEmptyLastName(){
        john.setLastName(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullRole(){
        john.setRole(null);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithNullCreatedBy(){
        john.setCreatedBy(null);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }

    @Test
    void saveUserWithEmptyCreatedBy(){
        john.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()->userIdentityOutputPort.save(john));
    }
    @Test
    @Order(3)
    void findAllUserByRole(){
        List<UserIdentity> userIdentities = new ArrayList<>();
        try{
            userIdentities = userIdentityOutputPort.findAllByRole(LOANEE);
        }catch (MeedlException exception){
            log.error("{} {}->",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(userIdentities);
        assertTrue( userIdentities.size()>0);
    }
    @Test
    @Order(4)
    void deleteUser(){
       try{
            UserIdentity existingUser = userIdentityOutputPort.findByEmail(john.getEmail());
            assertEquals(john.getId(),existingUser.getId());
            userIdentityOutputPort.deleteUserByEmail(john.getEmail());
       }catch (MeedlException exception){
           log.info("{} ->",exception.getMessage());
       }
        assertThrows(IdentityException.class,()-> userIdentityOutputPort.findByEmail(john.getEmail()));
    }


    @Test
    void deleteUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }


    @Test
    void deleteUserWithNonExistingEmail(){
        john.setEmail("invalid@gmail.com");
        assertThrows(IdentityException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithInvalidEmailFormat(){
        john.setEmail("invalid");
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserByEmail(john.getEmail()));
    }

    @Test
    void deleteUserWithUserId(){
        try{
            UserIdentity existingUser = userIdentityOutputPort.findById(john.getId());
            assertEquals(existingUser.getId(),john.getId());

            userIdentityOutputPort.deleteUserById(john.getId());
        }catch (MeedlException exception){
            log.info("{} ->",exception.getMessage());
        }
        assertThrows(IdentityException.class,()-> userIdentityOutputPort.findById(john.getId()));
    }

    @Test
    void deleteUserWithEmptyId(){
        john.setId(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserById(john.getId()));
    }
    @Test
    void deleteUserWithNullId(){
        john.setId(null);
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserById(john.getId()));
    }

    @Test
    void deleteUserWithNonExistingUserId(){
        john.setId("notvalid@gmail.com");
        assertThrows(MeedlException.class,()->userIdentityOutputPort.deleteUserById(john.getId()));
    }

    @Test
    void findUserByEmail(){
        try {
            UserIdentity existingUser = userIdentityOutputPort.findByEmail(john.getId());
            assertEquals(existingUser.getId(),john.getId());
            UserIdentity userIdentity =  userIdentityOutputPort.findByEmail(john.getEmail());
            assertNotNull(userIdentity);
        } catch (MeedlException e) {
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
        assertThrows(MeedlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  findUserWithNullEmail(){
        john.setEmail(null);
        assertThrows(MeedlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  findUserWithEmptyEmail(){
        john.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> userIdentityOutputPort.findByEmail(john.getEmail()));
    }

    @Test
    void  updateUser(){
        try {
            UserIdentity existingUser = userIdentityOutputPort.findByEmail(john.getEmail());
            assertEquals(existingUser.getFirstName(),john.getFirstName());
            assertNotNull(existingUser.getPhoneNumber());

            existingUser.setLastName("Johnny");
            UserIdentity updatedUser = userIdentityOutputPort.save(existingUser);

            UserIdentity findUpdatedUser = userIdentityOutputPort.findByEmail(updatedUser.getId());

            assertNotEquals(findUpdatedUser.getLastName(),john.getLastName());

        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }


    @AfterAll
    void cleanUp(){
        try {
            userIdentityOutputPort.deleteUserByEmail(john.getEmail());
        }catch (MeedlException meedlException){
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
    }


}