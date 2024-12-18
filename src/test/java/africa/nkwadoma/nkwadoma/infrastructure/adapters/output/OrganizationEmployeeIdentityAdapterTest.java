package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.test.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.util.*;

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
    private String organizationEmployeeIdentityId;

    @BeforeAll
    void init() {
        String testId = "ead0f7cb-5483-4bb8-b371-813970a9c367";
        UserIdentity joel = TestData.createTestUserIdentity("joel54@johnson.com");
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

            OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
            OrganizationEmployeeIdentity savedEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            assertNotNull(savedEmployeeIdentity);
            log.info("Saved employee identity: {}", savedEmployeeIdentity.getId());
            organizationEmployeeIdentityId = savedEmployeeIdentity.getId();
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
        List<OrganizationEmployeeIdentity> organizationEmployeeIdentities = new ArrayList<>();
        try{
            organizationEmployeeIdentities =
                    organizationEmployeeIdentityOutputPort.findEmployeesByNameAndRole(amazingGrace.getId(), "j", IdentityRole.ORGANIZATION_ADMIN);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        assertNotNull(organizationEmployeeIdentities);
        assertEquals(1, organizationEmployeeIdentities.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE,"hdhdh"})
    void findAllAdminInOrganizationWithInvalidId(String invalid){
        assertThrows(MeedlException.class,()-> organizationEmployeeIdentityOutputPort.findAllAdminInOrganization(invalid,
                IdentityRole.ORGANIZATION_ADMIN,pageSize,pageNumber));
    }

    @Test
    void findAllAdminInOrganization(){
        pageSize = 1;
        pageNumber = 0;
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities = null;
        try{
            organizationEmployeeIdentities =
                    organizationEmployeeIdentityOutputPort.findAllAdminInOrganization(amazingGrace.getId(),IdentityRole.ORGANIZATION_ADMIN,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error("Error finding organization employees", exception);
        }
        assertNotNull(organizationEmployeeIdentities);
        assertEquals(1, organizationEmployeeIdentities.getSize());
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