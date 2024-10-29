package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.time.*;
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

    @BeforeAll
    void init() {
        try {
            amazingGrace = new OrganizationIdentity();
            amazingGrace.setName("Amazing Grace Enterprises");
            amazingGrace.setEmail("rachel@gmail.com");
            amazingGrace.setInvitedDate(LocalDateTime.now().toString());
            amazingGrace.setRcNumber("RC345677");
            amazingGrace.setPhoneNumber("0907658483");
            amazingGrace.setTin("Tin5678");
            amazingGrace.setCreatedBy("0e08ce92-60dc-4374-8d5f-19b31cd8c781");
            amazingGrace.setServiceOfferings(List.of(ServiceOffering.builder().name(ServiceOfferingType.TRAINING.name()).
                    industry(Industry.EDUCATION).build()));
            amazingGrace.setWebsiteAddress("webaddress.org");

            joel = new UserIdentity();
            joel.setFirstName("Joel");
            joel.setLastName("Jacobs");
            joel.setEmail("joel@johnson.com");
            joel.setId(amazingGrace.getCreatedBy());
            joel.setPhoneNumber("098647748393");
            joel.setEmailVerified(Boolean.TRUE);
            joel.setEnabled(Boolean.TRUE);
            joel.setCreatedAt(LocalDateTime.now().toString());
            joel.setRole(IdentityRole.PORTFOLIO_MANAGER);
            joel.setCreatedBy(amazingGrace.getCreatedBy());

            amazingGrace.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().middlUser(joel).build()));
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);

            UserIdentity userIdentity = userIdentityOutputPort.save(joel);
            assertNotNull(userIdentity);
        } catch (MeedlException e) {
            log.error("Error saving organization", e);
        }
    }

    @Test
    void findAllOrganizationEmployees() {
        try {
            OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());
            UserIdentity userIdentity = userIdentityOutputPort.findByEmail(joel.getEmail());

            organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationIdentity.getId());
            organizationEmployeeIdentity.setMiddlUser(userIdentity);
            OrganizationEmployeeIdentity savedEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);
            assertNotNull(savedEmployeeIdentity);

            Page<OrganizationEmployeeIdentity> organizationEmployees =
                organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId(),
                        pageNumber, pageSize);

            assertNotNull(organizationEmployees);
        } catch (MeedlException e) {
            log.error("Error saving organization to the DB", e);
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
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        try {
            organizationIdentity = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());
        } catch (MeedlException e) {
            log.error("Error occurred ===>", e);
        }
        String id = organizationIdentity.getId();
        assertThrows(MeedlException.class, ()-> organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(id, pageNumber, pageSize));
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void viewAllOrganizationEmployeesWithInvalidPageSize(int pageSize) {
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        try {
            organizationIdentity = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());
        } catch (MeedlException e) {
            log.error("Error finding organization", e);
        }
        String id = organizationIdentity.getId();
        assertThrows(MeedlException.class, ()-> organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(id, pageNumber, pageSize));
    }


    @ParameterizedTest
    @ValueSource(strings = {"3a6d1124-1349-4f5b-831a-ac269369a90f"})
    void viewAllOrganizationEmployeesWithNonExistingOrganizationId(String organizationId) {
        assertThrows(MeedlException.class, () -> organizationEmployeeIdentityOutputPort.
                findAllOrganizationEmployees(organizationId, pageNumber, pageSize));
    }

    @AfterAll
    void tearDown() {
        try {
            OrganizationEmployeeIdentity employeeIdentity = organizationEmployeeIdentityOutputPort.findByCreatedBy(joel.getCreatedBy());
            organizationEmployeeIdentityOutputPort.delete(employeeIdentity.getId());
            userIdentityOutputPort.deleteUserByEmail(joel.getEmail());

            OrganizationIdentity organization = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationIdentityOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organization.getId());
            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationIdentityOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationIdentityOutputPort.deleteServiceOffering(serviceOfferingId);

            organizationIdentityOutputPort.delete(organization.getId());
        } catch (MeedlException e) {
            log.error("Error occurred cleaning up", e);
        }
    }
}