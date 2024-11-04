package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.*;
import java.math.BigInteger;
import java.util.List;

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
    private UserIdentity jack;
    private UserIdentity angela;
    private OrganizationEmployeeIdentity employeeJack;
    private OrganizationEmployeeIdentity employeeAngela;
    private String employeeJackId;
    private String employeeAngelaId;
    private OrganizationIdentity amazingGrace;
    private  UserIdentity joel;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private int pageNumber = 0;
    private int pageSize = 10;
    private String testId = "0e08ce92-60dc-4374-8d5f-19b31cd8c781";

    @BeforeEach
    void setUp() {

        jack = new UserIdentity();
        jack.setFirstName("Jack");
        jack.setLastName("Smith");
        jack.setEmail("jack@example.com");
        jack.setId(jack.getEmail());
        jack.setPhoneNumber("0987654321");
        jack.setEmailVerified(true);
        jack.setEnabled(true);
        jack.setCreatedAt(null);
        jack.setRole(IdentityRole.PORTFOLIO_MANAGER);
        jack.setCreatedBy("83f744df-78a2-4db6-bb04-b81545e78e49");

        angela = new UserIdentity();
        angela.setFirstName("Angela");
        angela.setLastName("Johnson");
        angela.setEmail("angela@example.com");
        angela.setId(angela.getEmail());
        angela.setPhoneNumber("09876543210");
        angela.setEmailVerified(true);
        angela.setEnabled(true);
        angela.setCreatedAt(null);
        angela.setRole(IdentityRole.PORTFOLIO_MANAGER);
        angela.setCreatedBy("83f744df-78a2-4db6-bb04-b81545e78e49");

        }
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
            amazingGrace.setCreatedBy(testId);
            amazingGrace.setId(testId);
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

            amazingGrace.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().meedlUser(joel).build()));
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);

            UserIdentity userIdentity = userIdentityOutputPort.save(joel);
            assertNotNull(userIdentity);
            jack = userIdentityOutputPort.save(jack);
            angela = userIdentityOutputPort.save(angela);
        } catch (MeedlException e) {
            log.error("Error saving user identity {}", e.getMessage());
        }

        employeeJack = OrganizationEmployeeIdentity.builder()
                .meedlUser(jack)
                .organization("83f744df-78a2-4db6-bb04-b81545e78e49")
               .build();
        employeeAngela = OrganizationEmployeeIdentity.builder()
                .meedlUser(angela)
                .organization("83f744df-78a2-4db6-bb04-b81545e78e49")
                .build();

    }
        @Test
        void findAllOrganizationEmployees() {
            try {
                OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());
                UserIdentity userIdentity = userIdentityOutputPort.findByEmail(joel.getEmail());

                organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
                organizationEmployeeIdentity.setOrganization(organizationIdentity.getId());
                organizationEmployeeIdentity.setMeedlUser(userIdentity);
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

    @Test
    @Order(1)
    void save() {
        OrganizationEmployeeIdentity jackSavedEmployee = organizationEmployeeIdentityOutputPort.save(employeeJack);
        employeeJackId = jackSavedEmployee.getId();
        OrganizationEmployeeIdentity angelaSavedEmployee = organizationEmployeeIdentityOutputPort.save(employeeAngela);
        employeeAngelaId = angelaSavedEmployee.getId();
        assertEquals(jackSavedEmployee.getMeedlUser().getEmail(), employeeJack.getMeedlUser().getEmail());
    }
    @Test
    @Order(2)
    void findAllByOrganization() {
        List<OrganizationEmployeeIdentity> organizationEmployees = null;
        try {
            organizationEmployees = organizationEmployeeIdentityOutputPort.findAllByOrganization(employeeAngela.getOrganization());
        } catch (MeedlException e) {
            log.error("Could not find organization employee {}", e.getMessage());
        }
        assertNotNull(organizationEmployees);
        assertFalse(organizationEmployees.isEmpty());
        assertTrue(organizationEmployees.size() > BigInteger.ONE.intValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewAllOrganizationEmployeesWithNullId(String id) {
        assertThrows(MeedlException.class, ()->organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(id, pageNumber, pageSize));
        }
    @Test
    void findAllByInvalidOrganizationUuid(){
        assertThrows(MeedlException.class, ()->organizationEmployeeIdentityOutputPort.findAllByOrganization("invalid uuid "));
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
            organizationIdentity = organizationIdentityOutputPort.findByEmail(amazingGrace.getEmail());
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
@Test
@Order(3)
void delete() {
    try {
        organizationEmployeeIdentityOutputPort.delete(employeeJackId);
        userIdentityOutputPort.deleteUserByEmail(jack.getEmail());

        organizationEmployeeIdentityOutputPort.delete(employeeAngelaId);
        userIdentityOutputPort.deleteUserByEmail(angela.getEmail());

        assertNull(organizationEmployeeIdentityOutputPort.findByEmployeeId(employeeAngelaId));
        assertNull(organizationEmployeeIdentityOutputPort.findByEmployeeId(employeeJackId));

        assertNull(userIdentityOutputPort.findByEmail(jack.getEmail()));
        userIdentityOutputPort.deleteUserByEmail(angela.getEmail());
        assertNull(userIdentityOutputPort.findByEmail(angela.getEmail()));
    } catch (MeedlException e) {
        log.error("Error deleting user identity {}", e.getMessage());
    }
}
}