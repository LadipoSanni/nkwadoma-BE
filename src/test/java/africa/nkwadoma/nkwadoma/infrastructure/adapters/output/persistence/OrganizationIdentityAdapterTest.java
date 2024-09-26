package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrganizationIdentityAdapterTest {
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    private OrganizationIdentity organization;

    @BeforeEach
        void setUp(){
        organization = new OrganizationIdentity();
        organization.setName("rachel");
        organization.setIndustry("Education");
        organization.setEmail("rachel@gmail.com");
        organization.setInvitedDate(LocalDateTime.now().toString());
        organization.setRcNumber("RX3456C");
        organization.setOrganizationId(organization.getRcNumber());
        organization.setPhoneNumber("0907658483");
        organization.setTin("Tin5678");
        organization.setWebsiteAddress("webaddress.org");
        }

    @Test
    void saveOrganization(){
            assertThrows(IdentityException.class,()-> organizationOutputPort.findById(organization.getOrganizationId()));
            try{
                OrganizationIdentity savedOrganization =  organizationOutputPort.save(organization);
                assertNotNull(savedOrganization);
                OrganizationIdentity foundOrganization = organizationOutputPort.findById(organization.getOrganizationId());
                assertEquals(foundOrganization.getIndustry(),savedOrganization.getIndustry());
             }catch (MiddlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
            }

       }

   @Test
    void saveUserWithExistingEmail(){
        try{
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organization);
            assertEquals(organization.getRcNumber(),savedOrganization.getRcNumber());
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
        organization.setEmail(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithEmptyEmail(){
        organization.setEmail(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithNullRcNumber(){
        organization.setRcNumber(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithEmptyTin(){
        organization.setTin(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }
    @Test
    void saveOrganizationWithInvalidEmailFormat(){
        organization.setEmail("invalid");
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }
    @Test
    void saveOrganizationWithNullTin(){
        organization.setTin(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithEmptyRcNumber(){
        organization.setRcNumber(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithEmptyIndustry(){
        organization.setIndustry(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithNullIndustry(){
        organization.setIndustry(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }
    @Test
    void saveOrganizationWithEmptyName(){
        organization.setName(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithNullName(){
        organization.setName(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithEmptyWebsiteAddress(){
        organization.setWebsiteAddress(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithNullWebsiteAddress(){
        organization.setWebsiteAddress(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }
    @Test
    void saveOrganizationWithEmptyPhoneNumber(){
        organization.setPhoneNumber(StringUtils.EMPTY);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void saveOrganizationWithNullPhoneNumber(){
        organization.setPhoneNumber(null);
        assertThrows(MiddlException.class, ()-> organizationOutputPort.save(organization));
    }

    @Test
    void findOrganization(){
       try{
           OrganizationIdentity organizationIdentity = organizationOutputPort.findById(organization.getRcNumber());
           assertNotNull(organizationIdentity);
           assertEquals(organizationIdentity.getIndustry(),organization.getIndustry());
       }catch (MiddlException middlException){
           log.info("{}", middlException.getMessage());
       }
    }

    @Test
    void findNonExistingOrganization(){
        organization.setOrganizationId("12345RC");
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(organization.getRcNumber()));
    }

    @Test
    void findNullOrganization(){
        organization.setOrganizationId(null);
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(organization.getOrganizationId()));
    }
    @Test
    void findEmptyOrganization(){
        organization.setOrganizationId(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> organizationOutputPort.findById(organization.getRcNumber()));
    }
    @Test
    void deleteOrganization() throws MiddlException {
        try{
            OrganizationIdentity foundUser = organizationOutputPort.findById(organization.getRcNumber());
            assertEquals(foundUser.getTin(),organization.getTin());
            organizationOutputPort.delete(organization.getRcNumber());
            assertThrows(IdentityException.class,()-> organizationOutputPort.findById(organization.getRcNumber()));
        } catch (MiddlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }

    }

    @Test
    void deleteWithNonExistingOrganizationRcNumber(){
        organization.setRcNumber("non existing101");
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(organization.getRcNumber()));
    }
    @Test
    void deleteWithNullOrganizationRcNumber(){
        organization.setRcNumber(null);
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(organization.getRcNumber()));
    }
    @Test
    void deleteWithEmptyOrganizationRcNumber(){
        organization.setRcNumber(null);
        assertThrows(MiddlException.class,()-> organizationOutputPort.delete(organization.getOrganizationId()));
    }
    @Test
    void updateOrganization(){
        try {
            OrganizationIdentity existingUser = organizationOutputPort.findById(organization.getOrganizationId());
            assertEquals(existingUser.getPhoneNumber(),organization.getPhoneNumber());
            assertNotNull(existingUser.getIndustry());

            existingUser.setName("Felicia");
            OrganizationIdentity updatedUser = organizationOutputPort.update(existingUser);

            OrganizationIdentity findUpdatedUser = organizationOutputPort.findById(updatedUser.getOrganizationId());

            assertNotEquals(findUpdatedUser.getName(),organization.getName());

        }catch (MiddlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void updateNullOrganization(){
        assertThrows(MiddlException.class,()-> organizationOutputPort.update(null));
    }


    @AfterAll
    void cleanUp(){
        try{
            organizationOutputPort.delete(organization.getOrganizationId());
        } catch (MiddlException e) {
            log.info("{} {}", e.getClass().getName(),e.getMessage());
        }
        }



}