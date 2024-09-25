package africa.nkwadoma.nkwadoma.domain.service;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class UserIdentityServiceTest {
    @Autowired
    private UserIdentityService userIdentityService;
    private UserIdentity mary;

    @BeforeEach
    void setUp(){
        mary = new UserIdentity();

        mary.setUserId(BigDecimal.ONE.toString());
        mary.setFirstName("John");
        mary.setLastName("Mary");
        mary.setEmail("mary4@johnson.com");
        mary.setPhoneNumber("0906777887");
        mary.setEmailVerified(true);
        mary.setEnabled(true);
        mary.setRole("PORTFOLIO_MANAGER");
        mary.setCreatedBy("Ken");


    }

    @Test
    void createUser(){
       try{
           UserIdentity savedMary = userIdentityService.createUser(mary);
           assertNotNull(savedMary);
       }catch (MiddlException exception){
           log.info("{}->",exception.getMessage());
       }
    }

    @Test
    void createUserWithSameEmail(){
        assertThrows(MiddlException.class, ()->userIdentityService.createUser(mary));
    }

    @Test
    void  createUserWithInvalidEmail(){
        mary.setEmail("iam.gmail.com");
        assertThrows(MiddlException.class, ()->userIdentityService.createUser(mary));
    }


}