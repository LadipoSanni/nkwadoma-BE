package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.keycloak.representations.idm.*;
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
        organizationIdentity = OrganizationIdentity.builder().email("org@example.com").
                name("My Organization").industry("My industry").rcNumber("56767").serviceOffering(ServiceOffering.builder().industry(Industry.EDUCATION).build()).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        program = Program.builder().name("My program").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
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
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void findProgramByName() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());

            assertNotNull(foundProgram);
            assertEquals(foundProgram.getName(), program.getName());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void findProgramById() {
        try {
            Program programByName = programOutputPort.findProgramByName(program.getName());

            Program foundProgram = programOutputPort.findProgramById(programByName.getId());

            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());
            assertEquals(programByName.getId(), foundProgram.getId());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void deleteProgram() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);

            programOutputPort.deleteProgram(foundProgram.getId());

            assertThrows(ResourceNotFoundException.class, ()-> programOutputPort.findProgramById(foundProgram.getId()));
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }
    @Test
    @Order(5)
    void saveProgramWithWrongIndustry() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            organization.setServiceOffering(ServiceOffering.builder().industry(Industry.BANKING).build());
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organization);
            assertNotNull(savedOrganization);
            assertEquals(Industry.BANKING, savedOrganization.getServiceOffering().getIndustry());

            program.setOrganizationId(savedOrganization.getId());

            assertThrows(EducationException.class, ()-> programOutputPort.saveProgram(program));

        } catch (MeedlException e) {
            log.info("{}", e.getMessage());
        }
    }

    @AfterAll
//    @Test
    void cleanUp() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            organizationOutputPort.delete(organization.getId());
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }

}