package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

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
    @Autowired
    private OrganizationEntityRepository organizationEntityRepository;

    @BeforeEach
        void setUp(){
        joel = TestData.createTestUserIdentity("joel@johnson.com");

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setMeedlUser(joel);
        List<OrganizationEmployeeIdentity> userIdentities = List.of(organizationEmployeeIdentity);

        amazingGrace = TestData.createOrganizationTestData("Amazing Grace Enterprises O'Neill", "RC87877", userIdentities);
    }

    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(amazingGraceId)) {
            try {
                List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                        findOrganizationServiceOfferingsByOrganizationId(amazingGraceId);
                String serviceOfferingId = null;
                for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                    serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                    organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
                }
                if (StringUtils.isNotEmpty(serviceOfferingId)) {
                    organizationOutputPort.deleteServiceOffering(serviceOfferingId);
                }
            } catch (MeedlException e) {
                log.error("", e);
            }
            organizationEntityRepository.deleteById(amazingGraceId);
        }
    }

    @Test
    @Order(1)
    void saveOrganization(){
            try{
                assertThrows(MeedlException.class,()-> organizationOutputPort.findById(amazingGrace.getId()));
                OrganizationIdentity savedOrganization =  organizationOutputPort.save(amazingGrace);
                log.info("Organization saved id is : {}", savedOrganization.getId());
                assertNotNull(savedOrganization);
                amazingGraceId = savedOrganization.getId();
                log.info("Organization saved successfully {}", savedOrganization);
                assertEquals(amazingGrace.getName(),savedOrganization.getName());
                assertNotNull(savedOrganization.getServiceOfferings());
                assertNotNull(savedOrganization.getServiceOfferings().get(0));
                assertEquals(amazingGrace.getServiceOfferings().get(0).getIndustry(),savedOrganization.getServiceOfferings().get(0).getIndustry());
             }catch (MeedlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
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
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void searchOrganizationWithInvalidName(String name) {
         assertThrows(MeedlException.class, () -> organizationOutputPort.findByName(name));
    }

    @Test
    @Order(2)
    void searchOrganizationByValidName(){
        List<OrganizationIdentity> organizationIdentities = null;
        try {
            OrganizationIdentity savedOrganization =  organizationOutputPort.save(amazingGrace);
            log.info("Saved Organization ID : {}", savedOrganization.getId());
            assertNotNull(savedOrganization);
            amazingGraceId = savedOrganization.getId();
            organizationIdentities = organizationOutputPort.findByName("a");
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertNotNull(organizationIdentities);
        assertFalse(organizationIdentities.isEmpty());
        log.info("{}", organizationIdentities);
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
    void viewAllOrganization(){
        try{
            int listSize = organizationOutputPort.viewAllOrganization(amazingGrace).toList().size();
            amazingGrace.setName("Amazing Grace Enterprises2");
            amazingGrace.setEmail("rachel2@gmail.com");
            joel.setEmail("joel2@johnson.com");
            OrganizationIdentity organizationIdentity = organizationOutputPort.save(amazingGrace);
            amazingGrace.setId(organizationIdentity.getId());

            Page<OrganizationIdentity> foundOrganizationIdentities = organizationOutputPort.viewAllOrganization(amazingGrace);
            assertNotNull(foundOrganizationIdentities);
            List<OrganizationIdentity> organizationIdentityList = foundOrganizationIdentities.toList();
            organizationOutputPort.delete(amazingGrace.getId());
            log.info("{}",organizationIdentityList.size());

            assertEquals(listSize + 1 , foundOrganizationIdentities.getTotalElements());
//            assertEquals(amazingGrace., foundOrganizationIdentities.getTotalPages());
            assertTrue(foundOrganizationIdentities.isFirst());
            assertTrue(foundOrganizationIdentities.isLast());

            assertNotNull(organizationIdentityList);
            assertEquals(listSize + 1, organizationIdentityList.size());
            assertEquals(organizationIdentityList.get(listSize).getName(), amazingGrace.getName());
            assertEquals(organizationIdentityList.get(listSize).getTin(), amazingGrace.getTin());
            assertEquals(organizationIdentityList.get(listSize).getEmail(), amazingGrace.getEmail());
            assertEquals(organizationIdentityList.get(listSize).getRcNumber(), amazingGrace.getRcNumber());

        }catch (MeedlException meedlException){
            log.info("{}", meedlException.getMessage());
        }
    }
    @Test
    void viewAllOrganizationWithNull(){
        assertThrows(MeedlException.class , ()-> organizationOutputPort.viewAllOrganization(null));

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