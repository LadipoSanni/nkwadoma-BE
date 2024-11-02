package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    private UserIdentityOutputPort userIdentityOutputPort;
    private UserIdentity jack;
    private UserIdentity angela;
    private OrganizationEmployeeIdentity employeeJack;
    private OrganizationEmployeeIdentity employeeAngela;
    private String employeeJackId;
    private String employeeAngelaId;
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

        try {
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
    @Test
    void findAllByInvalidOrganizationUuid(){
        assertThrows(MeedlException.class, ()->organizationEmployeeIdentityOutputPort.findAllByOrganization("invalid uuid "));
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