package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
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
    private String amazingGraceId;
    private  UserIdentity joel;
    @BeforeEach
        void setUp(){
        String testId = "ead0f7cb-5483-4bb8-b271-813970a9c368";
        joel = new UserIdentity();
        joel.setFirstName("Joel");
        joel.setLastName("Jacobs");
        joel.setEmail("joel@johnson.com");
        joel.setId(joel.getEmail());
        joel.setPhoneNumber("098647748393");
        joel.setEmailVerified(true);
        joel.setEnabled(true);
        joel.setCreatedAt(LocalDateTime.now().toString());
        joel.setRole(IdentityRole.PORTFOLIO_MANAGER);
        joel.setCreatedBy(testId);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setMeedlUser(joel);

        List<OrganizationEmployeeIdentity> userIdentities = List.of(organizationEmployeeIdentity);
        OrganizationEmployeeIdentity employeeJoel = new OrganizationEmployeeIdentity();
        employeeJoel.setMeedlUser(joel);

        amazingGrace = new OrganizationIdentity();
        amazingGrace.setName("Amazing Grace Enterprises");
        amazingGrace.setEmail("rachel@gmail.com");
        amazingGrace.setInvitedDate(LocalDateTime.now().toString());
        amazingGrace.setRcNumber("RC345677");
        amazingGrace.setId(testId);
        amazingGrace.setPhoneNumber("0907658483");
        amazingGrace.setTin("Tin5678");
        amazingGrace.setServiceOfferings(List.of(new ServiceOffering()));
        amazingGrace.getServiceOfferings().get(0).setIndustry(Industry.BANKING);
        amazingGrace.setWebsiteAddress("webaddress.org");
        amazingGrace.setOrganizationEmployees(userIdentities);
        amazingGrace.setOrganizationEmployees(List.of(employeeJoel));
        }

    @Test
    void saveOrganization(){
            try{
                assertThrows(MeedlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
                OrganizationIdentity savedOrganization =  organizationOutputPort.save(amazingGrace);
                log.info("Organization saved id is : {}", savedOrganization.getId());
                amazingGraceId = savedOrganization.getId();
                assertNotNull(savedOrganization);
                assertEquals(amazingGrace.getName(),savedOrganization.getName());
                assertNotNull(savedOrganization.getServiceOfferings());
                assertNotNull(savedOrganization.getServiceOfferings().get(0));
                assertEquals(amazingGrace.getServiceOfferings().get(0).getIndustry(),savedOrganization.getServiceOfferings().get(0).getIndustry());
             }catch (MeedlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
            }

       }

   @Test
    void saveOrganizationWithExistingRcNumber() {
       try {
           OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(amazingGrace.getEmail());
           log.info("Save organization with existing rc. id is {} ", foundOrganization);
           assertEquals(amazingGrace.getRcNumber(), foundOrganization.getRcNumber());
           assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
       } catch (MeedlException exception) {
           log.info("{} {}->", exception.getClass().getName(), exception.getMessage());
       }
   }

    @Test
    void saveOrganizationWithNullOrganizationIdentity(){
       assertThrows(MeedlException.class, ()-> organizationOutputPort.save(null));
    }

    @Test
    void saveOrganizationWithNullEmail(){
        amazingGrace.setEmail(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyEmail(){
        amazingGrace.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullRcNumber(){
        amazingGrace.setRcNumber(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyRcNumber(){
        amazingGrace.setRcNumber(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithInvalidEmailFormat(){
        amazingGrace.setEmail("invalid");
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullIndustry(){
        amazingGrace.getServiceOfferings().get(0).setIndustry(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }
    @Test
    void saveOrganizationWithNullServiceOffering(){
        amazingGrace.setServiceOfferings(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }
    @Test
    void saveOrganizationWithEmptyName(){
        amazingGrace.setName(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullName(){
        amazingGrace.setName(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyPhoneNumber(){
        amazingGrace.setPhoneNumber(StringUtils.EMPTY);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullPhoneNumber(){
        amazingGrace.setPhoneNumber(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullAdmin(){
        amazingGrace.setOrganizationEmployees(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyAdmin(){
        amazingGrace.setOrganizationEmployees(Collections.emptyList());
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithIncompleteAdminField(){
        joel.setEmail(null);
        joel.setPhoneNumber(null);
        joel.setFirstName(null);
        joel.setLastName(null);
        joel.setCreatedBy(null);
        assertThrows(MeedlException.class, ()-> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void findOrganization(){
       try{
           OrganizationIdentity organizationIdentity = organizationOutputPort.findById(amazingGrace.getId());
           assertNotNull(organizationIdentity);
           assertEquals(organizationIdentity.getName(), amazingGrace.getName());
       }catch (MeedlException meedlException){
           log.info("{}", meedlException.getMessage());
       }
    }

    @Test
    void findNonExistingOrganization(){
        amazingGrace.setId("12345RC");
        assertThrows(MeedlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }

    @Test
    void findNullOrganization(){
        amazingGrace.setId(null);
        assertThrows(MeedlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }
    @Test
    void findEmptyOrganization(){
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
    }
    @Test
    void deleteOrganization(){
        try{
            OrganizationIdentity foundUser = organizationOutputPort.findById(amazingGrace.getId());
            assertEquals(foundUser.getTin(), amazingGrace.getTin());
            organizationOutputPort.delete(amazingGrace.getId());
            assertThrows(ResourceNotFoundException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
        } catch (MeedlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
    }

    @Test
    void deleteWithNonExistingOrganizationId(){
        amazingGrace.setId("non existing101");
        assertThrows(MeedlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void deleteWithNullOrganizationId(){
        amazingGrace.setId(null);
        assertThrows(MeedlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void deleteWithEmptyOrganizationId(){
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> organizationOutputPort.delete(amazingGrace.getId()));
    }
    @Test
    void updateOrganization(){
        try {
            OrganizationIdentity existingUser = organizationOutputPort.findById(amazingGrace.getId());
            assertEquals(existingUser.getPhoneNumber(), amazingGrace.getPhoneNumber());
            assertNotNull(existingUser.getServiceOfferings().get(0).getIndustry());

            existingUser.setName("Felicia");
            OrganizationIdentity updatedUser = organizationOutputPort.save(existingUser);

            OrganizationIdentity findUpdatedUser = organizationOutputPort.findById(updatedUser.getId());

            assertNotEquals(findUpdatedUser.getName(), amazingGrace.getName());

        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @AfterAll
    void cleanUp(){
        try{
            organizationOutputPort.delete(amazingGrace.getId());
        } catch (MeedlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
    }

}