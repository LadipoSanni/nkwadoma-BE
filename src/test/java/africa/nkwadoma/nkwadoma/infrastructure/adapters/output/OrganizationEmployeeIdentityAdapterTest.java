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
    private  UserIdentity joel;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private int pageNumber = 0;
    private int pageSize = 10;
    private String organizationId;
    private OrganizationEmployeeIdentity savedEmployeeIdentity;
    private String userId;
    private String organizationEmployeeIdentityId;

    @BeforeAll
    void init() {
        try {
            joel = TestData.createTestUserIdentity("joel54@johnson.com");
            List<OrganizationEmployeeIdentity> employees = List.of(OrganizationEmployeeIdentity
                    .builder().meedlUser(joel).build());

            amazingGrace = TestData.createOrganizationTestData(
                    "Amazing Grace Enterprises",
                    "RC79500034",
                    employees
            );
            amazingGrace.setServiceOfferings(List.of(ServiceOffering.builder().
                    name(ServiceOfferingType.TRAINING.name()).
                    industry(Industry.EDUCATION).build()));

            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);
            organizationId = savedOrganization.getId();

            joel = userIdentityOutputPort.save(joel);
            assertNotNull(joel);
            userId = joel.getId();

            organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
            savedEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            assertNotNull(savedEmployeeIdentity);
            organizationEmployeeIdentityId = savedEmployeeIdentity.getId();
        } catch (MeedlException e) {
            log.error("Error saving organization", e);
        }
    }

    @Test
    void findAllOrganizationEmployees() {
        try {
            Page<OrganizationEmployeeIdentity> organizationEmployees =
                    organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(
                            organizationId, pageNumber, pageSize);

            assertNotNull(organizationEmployees);
            OrganizationEmployeeIdentity employee = organizationEmployees.getContent().get(0);
            assertEquals(1, organizationEmployees.getContent().size());
            assertEquals("John", employee.getMeedlUser().getFirstName());
            assertEquals("Doe", employee.getMeedlUser().getLastName());
            assertEquals(ActivationStatus.INVITED, employee.getStatus());
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