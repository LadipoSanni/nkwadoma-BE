package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramPersistenceAdapterTest {
    @Autowired
    private ProgramOutputPort programOutputPort;
    private Program program;
    private OrganizationIdentity organizationIdentity;
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    private UserIdentity userIdentity;

    @BeforeEach
    void setUp() {
        userIdentity = UserIdentity.builder().firstName("Fred").role("PORTFOLIO_MANAGER").
                lastName("Benson").email("fred@example.com").createdBy("8937-b9897g3-bv38").build();

        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder()
                .middlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder().
                id("9bb328d3-2bf4-4ad1-95d0-818a72734d00").email("org@example.com").
                name("My Organization").industry("My industry").rcNumber("56767").
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        program = Program.builder().name("My program").
                programStatus(ProgramStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).deliveryType(DeliveryType.ONSITE).
                programType(ProgramType.PROFESSIONAL).createdAt(LocalDateTime.now()).createdBy("68379").programStartDate(LocalDate.now()).
                build();
    }

    @Test
    @Order(1)
    void saveProgram() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            assertNotNull(savedOrganization);

            program.setOrganizationId(savedOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(program.getName(), savedProgram.getName());
            assertEquals(program.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(program.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(program.getProgramType(), savedProgram.getProgramType());
            assertEquals(program.getProgramStartDate(), savedProgram.getProgramStartDate());
            programOutputPort.deleteProgram(savedProgram.getId());
            organizationOutputPort.delete(savedOrganization.getId());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void findProgramByName() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            assertNotNull(savedOrganization);

            program.setOrganizationId(savedOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);

            Program foundProgram = programOutputPort.findProgramByName(program.getName());

            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());
            assertEquals(savedProgram.getId(), foundProgram.getId());
            programOutputPort.deleteProgram(savedProgram.getId());
            organizationOutputPort.delete(savedOrganization.getId());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void findProgramById() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            assertNotNull(savedOrganization);

            program.setOrganizationId(savedOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);

            Program foundProgram = programOutputPort.findProgramById(savedProgram.getId());

            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());
            assertEquals(savedProgram.getId(), foundProgram.getId());
            programOutputPort.deleteProgram(savedProgram.getId());
            organizationOutputPort.delete(savedOrganization.getId());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void deleteProgram() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            assertNotNull(savedOrganization);

            program.setOrganizationId(savedOrganization.getId());

            Program savedProgram = programOutputPort.saveProgram(program);
            Program foundProgram = programOutputPort.findProgramById(savedProgram.getId());
            assertNotNull(foundProgram);

            programOutputPort.deleteProgram(foundProgram.getId());
            organizationOutputPort.delete(savedOrganization.getId());

            Program deletedProgram = programOutputPort.findProgramById(savedProgram.getId());
            assertNull(deletedProgram);
        } catch (MeedlException e) {
            log.info("{}", e.getMessage());
        }
    }
}