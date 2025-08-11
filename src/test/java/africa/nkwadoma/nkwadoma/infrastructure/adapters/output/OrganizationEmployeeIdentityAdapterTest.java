package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationEmployeeIdentityAdapterTest {
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private OrganizationIdentity amazingGrace;
    private int pageNumber = 0;
    private int pageSize = 10;
    private String organizationId;
    private String userId;
    private UserIdentity joel;
    private String organizationEmployeeIdentityId;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;


    @BeforeAll
    void init() {
        String testId = "ead0f7cb-5483-4bb8-b371-813970a9c367";
        joel = TestData.createTestUserIdentity("joel54@johnson.com");
        joel.setId(testId);
        joel.setRole(IdentityRole.ORGANIZATION_ADMIN);
        List<OrganizationEmployeeIdentity> employees = List.of(OrganizationEmployeeIdentity
                .builder().meedlUser(joel).build());
        amazingGrace = TestData.createOrganizationTestData(
                "Amazing Grace Enterprises",
                "RC7950004",
                employees
        );
        try {
            OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(amazingGrace.getId());
            organizationIdentityOutputPort.delete(organizationIdentity.getId());
        }catch (MeedlException e){
            log.info("Couldn't find or delete organization with id {}", amazingGrace.getId());
        }
        try {

            amazingGrace.setId(testId);
            amazingGrace.setServiceOfferings(List.of(ServiceOffering.builder().
                    name(ServiceOfferingType.TRAINING.name()).
                    industry(Industry.EDUCATION).build()));

            log.info("Saving the organization for testing.");
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);
            log.info("Saved organization for testing : {}",savedOrganization.getId());
            organizationId = savedOrganization.getId();

            joel = userIdentityOutputPort.save(joel);
            assertNotNull(joel);
            userId = joel.getId();

             organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
             organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getMeedlRoles());
            organizationEmployeeIdentity.setActivationStatuses(Set.of(ActivationStatus.ACTIVE));

            assertNotNull(organizationEmployeeIdentity);
            log.info("Saved employee identity: {}", organizationEmployeeIdentity.getId());
            organizationEmployeeIdentityId = organizationEmployeeIdentity.getId();
        } catch (MeedlException e) {
            log.error("Error saving organization : {}", e.getMessage());
        }
    }

    @Test
    void findAllOrganizationEmployees() {
        try {
            Page<OrganizationEmployeeIdentity> organizationEmployees =
                    organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(
                            amazingGrace.getId(), pageNumber, pageSize);

            assertNotNull(organizationEmployees);
            OrganizationEmployeeIdentity employee = organizationEmployees.getContent().get(0);
            assertEquals(1, organizationEmployees.getContent().size());
            assertEquals("John", employee.getMeedlUser().getFirstName());
            assertEquals("Doe", employee.getMeedlUser().getLastName());
            assertEquals("joel54@johnson.com", employee.getMeedlUser().getEmail());
//            assertEquals(ActivationStatus.INVITED, employee.getStatus());
        } catch (MeedlException e) {
            log.error("Error retrieving organization employees", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewAllOrganizationEmployeesWithNullId(String id) {
        assertThrows(MeedlException.class, ()->organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(id, pageNumber, pageSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {"03945988", "non-uuid"})
    void viewAllOrganizationEmployeesWithNonUUIDId(String id) {
        assertThrows(MeedlException.class, ()->organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(id, pageNumber, pageSize));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1})
    void viewAllOrganizationEmployeesWithInvalidPageNumber(int pageNumber) {
        assertThrows(MeedlException.class, ()-> organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(organizationId, pageNumber, pageSize));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void viewAllOrganizationEmployeesWithInvalidPageSize(int pageSize) {
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        try {
            organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        } catch (MeedlException e) {
            log.error("Error finding organization employees", e);
        }
        String id = organizationIdentity.getId();
        assertThrows(MeedlException.class, ()-> organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(id, pageNumber, pageSize));
    }

    @Test
    void findEmployeeByNameAndRoleTest(){
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities = Page.empty();
        try{
            OrganizationIdentity organization = OrganizationIdentity.builder().name("A").id(amazingGrace.getId())
                    .pageNumber(pageNumber).pageSize(pageSize).build();
            organizationEmployeeIdentities =
                    organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(organization.getId(), organizationEmployeeIdentity);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        log.info("organization employess {}", organizationEmployeeIdentities);
        assertNotNull(organizationEmployeeIdentities);
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE,"hdhdh"})
    void findAllAdminInOrganizationWithInvalidId(String invalidOrganizationId){
        assertThrows(MeedlException.class,()-> organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(invalidOrganizationId,
                organizationEmployeeIdentity));
    }

    @Test
    void findAllAdminInOrganization(){
        organizationEmployeeIdentity.setPageSize(1);
        organizationEmployeeIdentity.setPageNumber(0);
        organizationEmployeeIdentity.setIdentityRoles(Set.of(IdentityRole.ORGANIZATION_ADMIN));
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities = null;
        try{
            organizationEmployeeIdentities =
                    organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(amazingGrace.getId(),
                            organizationEmployeeIdentity);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        assertNotNull(organizationEmployeeIdentities);
        assertEquals(1, organizationEmployeeIdentities.getSize());
    }

    @Test
    void findAllAdminInOrganizationReturningList(){
        List<OrganizationEmployeeIdentity> organizationEmployeeIdentities = new ArrayList<>();
        try{
            organizationEmployeeIdentities =
                    organizationEmployeeIdentityOutputPort.findAllEmployeesInOrganizationByOrganizationIdAndRole(amazingGrace.getId(),IdentityRole.ORGANIZATION_ADMIN);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        assertNotNull(organizationEmployeeIdentities);
        assertEquals(1, organizationEmployeeIdentities.size());
    }

    @Test
    void findOrganizationEmployeeByOrganizationIdAndRole(){
        OrganizationEmployeeIdentity organizationEmployeeIdentity= null;
        try{
            organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByRoleAndOrganizationId(organizationId,IdentityRole.ORGANIZATION_ADMIN);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        assertNotNull(organizationEmployeeIdentity);
        assertEquals(organizationEmployeeIdentity.getOrganization(), organizationId);
    }

    @Test
    void findOrganizationEmployeeByNullOrganizationIdAndRole(){
        assertThrows(MeedlException.class,()-> organizationEmployeeIdentityOutputPort.findByRoleAndOrganizationId(null,IdentityRole.ORGANIZATION_ADMIN));
    }


//    @Test
//    @Order(1)
//    void searchAdminsByValidName() throws MeedlException {
//        organizationEmployeeIdentity.setName(joel.getFirstName());
//        organizationEmployeeIdentity.setIdentityRoles(Set.of(joel.getRole()));
//        Page<OrganizationEmployeeIdentity> result = organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(
//                organizationId, organizationEmployeeIdentity);
//
//        assertThat(result.getTotalElements()).isEqualTo(1);
//    }
//    @Test
//    @Order(2)
//    void searchAdminsByValidEmail() throws MeedlException {
//        organizationEmployeeIdentity.setName(joel.getEmail());
//        Page<OrganizationEmployeeIdentity> result = organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(
//                organizationId, organizationEmployeeIdentity);
//
//        assertThat(result.getTotalElements()).isEqualTo(1);
//    }
//
//
//    @Test
//    @Order(3)
//    void searchNameWithNoMatch() throws MeedlException {
//        organizationEmployeeIdentity.setName("no match");
//        Page<OrganizationEmployeeIdentity> result = organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(
//                organizationId, organizationEmployeeIdentity);
//        assertThat(result.getTotalElements()).isZero();
//    }

    @Test
    @Order(4)
    void searchWithInvalidOrganizationId() {
        assertThrows(MeedlException.class, () -> {
            organizationEmployeeIdentityOutputPort.searchOrFindAllAdminInOrganization(
                    "not-a-uuid", organizationEmployeeIdentity);
        });
    }

    @AfterAll
    void tearDown() {
        try {
            organizationEmployeeIdentityOutputPort.delete(organizationEmployeeIdentityId);
            userIdentityOutputPort.deleteUserById(userId);

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationIdentityOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organizationId);
            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationIdentityOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationIdentityOutputPort.deleteServiceOffering(serviceOfferingId);

            organizationIdentityOutputPort.delete(organizationId);
        } catch (MeedlException e) {
            log.error("Error occurred cleaning up", e);
        }
    }
}