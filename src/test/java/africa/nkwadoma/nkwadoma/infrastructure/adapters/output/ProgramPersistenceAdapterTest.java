package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.hibernate.annotations.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
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
    private OrganizationIdentity bankingOrganization;

    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    private UserIdentity userIdentity;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("Joel");
        userIdentity.setLastName("Jacobs");
        userIdentity.setEmail("joel@johnson.com");
        userIdentity.setId(userIdentity.getEmail());
        userIdentity.setPhoneNumber("098647748393");
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity.setRole(PORTFOLIO_MANAGER);
        userIdentity.setCreatedBy("Ayo");

        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().middlUser(userIdentity).build();
        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setName("Amazing Grace Enterprises");
        organizationIdentity.setEmail("rachel@gmail.com");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setRcNumber("RC345677");
        organizationIdentity.setId(organizationIdentity.getRcNumber());
        organizationIdentity.setPhoneNumber("0907658483");
        organizationIdentity.setTin("Tin5678");
        organizationIdentity.setNumberOfPrograms(0);
        ServiceOffering serviceOffering = new ServiceOffering();
        serviceOffering.setIndustry(Industry.EDUCATION);
        organizationIdentity.setServiceOffering(serviceOffering);
        organizationIdentity.setWebsiteAddress("webaddress.org");
        organizationIdentity.setOrganizationEmployees(List.of(employeeIdentity));

        bankingOrganization = new OrganizationIdentity();

        program = new Program();
        program.setName("My program");
        program.setProgramDescription("My program description");
        program.setMode(ProgramMode.FULL_TIME);
        program.setProgramStatus(ActivationStatus.ACTIVE);
        program.setDuration(2);
        program.setDeliveryType(DeliveryType.ONSITE);
        program.setCreatedBy(userIdentity.getCreatedBy());
        program.setDurationType(DurationType.MONTHS);
    }


    @Test
    void saveProgram() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            OrganizationIdentity foundOrganization = organizationOutputPort.findById(savedOrganization.getId());
            assertNotNull(foundOrganization);
            assertNotNull(foundOrganization.getId());

            program.setOrganizationId(foundOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(program.getName(), savedProgram.getName());
            assertEquals(program.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(program.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

//    @Test
//    void saveProgramWithWrongIndustry() {
//        try {
//            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
//            organization.setServiceOffering(ServiceOffering.builder().industry(Industry.BANKING).build());
//
//            OrganizationIdentity savedOrganization = organizationOutputPort.save(organization);
//            assertNotNull(savedOrganization);
//            assertEquals(Industry.BANKING, savedOrganization.getServiceOffering().getIndustry());
//
//            Program foundProgram = programOutputPort.findProgramByName(program.getName());
//            foundProgram.setOrganizationId(savedOrganization.getId());
//
//            assertThrows(MeedlException.class, ()-> programOutputPort.saveProgram(foundProgram));
//        } catch (MeedlException e) {
//            log.error("Error while saving program", e);
//        }
//    }

    @Test
    void findProgramByName() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());

            assertNotNull(foundProgram);
            assertEquals(foundProgram.getName(), program.getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
    }

    @Test
    void findProgramById() {
        try {
            Program foundProgramByName = programOutputPort.findProgramByName(program.getName());

            Program foundProgram = programOutputPort.findProgramById(foundProgramByName.getId());

            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by ID", e);
        }
    }

    @Test
    void findAllPrograms() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertNotNull(organization);
            assertNotNull(organization.getId());

            program.setOrganizationId(organization.getId());
            Page<Program> foundPrograms = programOutputPort.findAllPrograms(program.getOrganizationId(), pageSize, pageNumber);
            List<Program> programsList = foundPrograms.toList();

            assertEquals(1, foundPrograms.getTotalElements());
            assertEquals(1, foundPrograms.getTotalPages());
            assertTrue(foundPrograms.isFirst());
            assertTrue(foundPrograms.isLast());

            assertNotNull(programsList);
            assertEquals(1, programsList.size());
            assertEquals(programsList.get(0).getName(), program.getName());
            assertEquals(programsList.get(0).getDuration(), program.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), program.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfTrainees(), program.getNumberOfTrainees());
        } catch (MeedlException e) {
            log.error("Error finding all programs", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "f98hv"})
    void findAllProgramsByNullOrInvalidOrganizationId(String organizationId) {
        try {
            Page<Program> foundPrograms = programOutputPort.findAllPrograms(organizationId, pageSize, pageNumber);
            List<Program> foundProgramsList = foundPrograms.toList();
            assertTrue(foundProgramsList.isEmpty());
            assertEquals(foundProgramsList, List.of());
        } catch (MeedlException e) {
            log.error("Failed to find all programs", e);
        }
    }

    @Test
    void deleteProgram() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());

            programOutputPort.deleteProgram(foundProgram.getId());

            assertThrows(ResourceNotFoundException.class, ()-> programOutputPort.findProgramById(program.getId()));
        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
    }


    @AfterAll
    void cleanUp() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            programOutputPort.deleteProgram(foundProgram.getId());

            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertNotNull(organization);
            organizationIdentity.setId(organization.getId());
            organizationOutputPort.delete(organizationIdentity.getId());
            assertThrows(ResourceNotFoundException.class, ()-> organizationOutputPort.findById(organizationIdentity.getId()));
        } catch (MeedlException e) {
            log.error("Error deleting program", e);
        }
    }

}