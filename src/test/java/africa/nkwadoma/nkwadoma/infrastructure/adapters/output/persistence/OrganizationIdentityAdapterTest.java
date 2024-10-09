package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrganizationIdentityAdapterTest {
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    private OrganizationIdentity amazingGrace;
    private  UserIdentity joel;
    @BeforeEach
        void setUp(){
        joel = new UserIdentity();
        joel.setFirstName("Joel");
        joel.setLastName("Jacobs");
        joel.setEmail("joel@johnson.com");
        joel.setId(joel.getEmail());
        joel.setPhoneNumber("098647748393");
        joel.setEmailVerified(true);
        joel.setEnabled(true);
        joel.setCreatedAt(LocalDateTime.now().toString());
        joel.setRole("ADMIN");
        joel.setCreatedBy("Ayo");

//        List<UserIdentity> organizationAdmin = new ArrayList<>();
//        organizationAdmin.add(joel);

        amazingGrace = new OrganizationIdentity();
        amazingGrace.setName("Amazing Grace Enterprises");
        amazingGrace.setIndustry("Education");
        amazingGrace.setEmail("rachel@gmail.com");
        amazingGrace.setInvitedDate(LocalDateTime.now().toString());
        amazingGrace.setRcNumber("RC345677");
        amazingGrace.setId(amazingGrace.getRcNumber());
        amazingGrace.setPhoneNumber("0907658483");
        amazingGrace.setTin("Tin5678");
        amazingGrace.setWebsiteAddress("webaddress.org");
       // amazingGrace.setOrganizationAdmins(organizationAdmin);
        }

    @Test
    void saveOrganization(){
            try{
                assertThrows(IdentityException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
                OrganizationIdentity savedOrganization =  organizationOutputPort.save(amazingGrace);
                assertNotNull(savedOrganization);
                OrganizationIdentity foundOrganization = organizationOutputPort.findById(amazingGrace.getId()).get();
                assertEquals(foundOrganization.getName(),savedOrganization.getName());
                assertEquals(foundOrganization.getIndustry(),savedOrganization.getIndustry());
             }catch (MiddlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
            }

       }


   @Test
    void saveOrganizationWithExistingRcNumber(){
        try{
            OrganizationIdentity foundOrganization = organizationOutputPort.findById(amazingGrace.getId()).get();
            assertEquals(amazingGrace.getRcNumber(), foundOrganization.getRcNumber());
            OrganizationIdentity savedOrganization = organizationOutputPort.save(amazingGrace);
            assertEquals(amazingGrace.getId(),savedOrganization.getId());
        }catch (MiddlException exception){
            log.info("{} {}->",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveOrganizationWithNullOrganizationIdentity(){
       assertThrows(MiddlException.class, ()-> organizationOutputPort.save(null));
    }

    @Test
    void saveOrganizationWithNullEmail(){
        amazingGrace.setEmail(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyEmail(){
        amazingGrace.setEmail(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullRcNumber(){
        amazingGrace.setRcNumber(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyRcNumber(){
        amazingGrace.setRcNumber(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithInvalidEmailFormat(){
        amazingGrace.setEmail("invalid");
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }
    @Test
    void saveOrganizationWithEmptyIndustry(){
        amazingGrace.setIndustry(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullIndustry(){
        amazingGrace.setIndustry(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }
    @Test
    void saveOrganizationWithEmptyName(){
        amazingGrace.setName(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullName(){
        amazingGrace.setName(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyPhoneNumber(){
        amazingGrace.setPhoneNumber(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullPhoneNumber(){
        amazingGrace.setPhoneNumber(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullAdmin(){
        //amazingGrace.setOrganizationAdmins(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

//    @Test
//    void saveOrganizationWithEmptyAdmin(){
//        amazingGrace.setOrganizationAdmins(Collections.EMPTY_LIST);
//        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
//    }

    @Test
    void saveOrganizationWithIncompleteAdminField(){
        joel.setEmail(null);
        joel.setPhoneNumber(null);
        joel.setFirstName(null);
        joel.setLastName(null);
        joel.setCreatedBy(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void findOrganization(){
       try{
           OrganizationIdentity organizationIdentity = organizationOutputPort.findById(amazingGrace.getId()).get();
           assertNotNull(organizationIdentity);
           assertEquals(organizationIdentity.getIndustry(), amazingGrace.getIndustry());
       }catch (MiddlException middlException){
           log.info("{}", middlException.getMessage());
       }
    }

    @Test
    void findNonExistingOrganization(){
        amazingGrace.setId("12345RC");
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }

    @Test
    void findNullOrganization(){
        amazingGrace.setId(null);
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }
    @Test
    void findEmptyOrganization(){
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }
    @Test
    void deleteOrganization(){
        try{
            OrganizationIdentity foundUser = organizationOutputPort.findById(amazingGrace.getId()).get();
            assertEquals(foundUser.getTin(), amazingGrace.getTin());
            organizationOutputPort.delete(amazingGrace.getId());
            assertThrows(IdentityException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
        } catch (MiddlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
    }

    @Test
    void deleteWithNonExistingOrganizationId(){
        amazingGrace.setId("non existing101");
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void deleteWithNullOrganizationId(){
        amazingGrace.setId(null);
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void deleteWithEmptyOrganizationId(){
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void updateOrganization(){
        try {
            OrganizationIdentity existingUser = organizationOutputPort.findById(amazingGrace.getId()).get();
            assertEquals(existingUser.getPhoneNumber(), amazingGrace.getPhoneNumber());
            assertNotNull(existingUser.getIndustry());

            existingUser.setName("Felicia");
            OrganizationIdentity updatedUser = organizationOutputPort.save(existingUser);

            OrganizationIdentity findUpdatedUser = organizationOutputPort.findById(updatedUser.getId()).get();

            assertNotEquals(findUpdatedUser.getName(), amazingGrace.getName());

        }catch (MiddlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @AfterAll
    void cleanUp(){
        try{
            organizationOutputPort.delete(amazingGrace.getId());
        } catch (MiddlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
    }

}