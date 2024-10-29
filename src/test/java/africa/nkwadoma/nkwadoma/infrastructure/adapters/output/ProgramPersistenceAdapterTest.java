package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
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
import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramPersistenceAdapterTest {
    @Autowired
    private ProgramOutputPort programOutputPort;
    private Program program;
    private Program designThinking;
    private OrganizationIdentity organizationIdentity;
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private UserIdentity userIdentity;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        program = new Program();
        program.setName("Software Engineering");
        program.setProgramDescription("A rigorous course in the art and science of software engineering");
        program.setMode(ProgramMode.FULL_TIME);
        program.setProgramStatus(ActivationStatus.ACTIVE);
        program.setDuration(2);
        program.setDeliveryType(DeliveryType.ONSITE);
        program.setDurationType(DurationType.MONTHS);

        designThinking = new Program();
        designThinking.setName("Design Thinking");
        designThinking.setProgramDescription("The art of putting thought into solving problems");
        designThinking.setMode(ProgramMode.FULL_TIME);
        designThinking.setProgramStatus(ActivationStatus.ACTIVE);
        designThinking.setDuration(1);
        designThinking.setDeliveryType(DeliveryType.ONSITE);
        designThinking.setDurationType(DurationType.YEARS);
    }


    @BeforeAll
    void init() {
        try {
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

            OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().
                    middlUser(userIdentity).build();
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
            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
            serviceOffering.setIndustry(Industry.EDUCATION);
            organizationIdentity.setServiceOfferings(List.of(serviceOffering));
            organizationIdentity.setWebsiteAddress("webaddress.org");
            organizationIdentity.setOrganizationEmployees(List.of(employeeIdentity));

            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            userIdentityOutputPort.save(userIdentity);
            organizationIdentity.getOrganizationEmployees().forEach(employeeIdentityOutputPort::save);

            OrganizationIdentity foundOrganization = organizationOutputPort.findById(savedOrganization.getId());
            assertNotNull(foundOrganization);
            assertNotNull(foundOrganization.getId());
        } catch (MeedlException e) {
            log.error("Error creating organization", e);
        }
    }

    @Test
    @Order(1)
    void saveProgram() {
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());

            program.setOrganizationId(foundOrganization.getId());
            program.setCreatedBy(userIdentity.getCreatedBy());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(program.getName(), savedProgram.getName());
            assertEquals(program.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(program.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
            programOutputPort.deleteProgram(savedProgram.getId());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

    @Test
    void createProgramWithNullProgram(){
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((null)));
    }

    @Test
    void saveProgramWithNonTrainingServiceOffering() {
        try {
            organizationIdentity.setServiceOfferings(List.of(ServiceOffering.builder()
                    .name("NON_TRAINING").industry(Industry.BANKING).build()));
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            assertNotNull(savedOrganization);

            designThinking.setOrganizationId(savedOrganization.getId());
            designThinking.setCreatedBy(userIdentity.getCreatedBy());

            assertThrows(MeedlException.class, ()-> programOutputPort.saveProgram(designThinking));

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(savedOrganization.getId());

            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationOutputPort.deleteServiceOffering(serviceOfferingId);
            organizationOutputPort.delete(savedOrganization.getId());
        } catch (MeedlException e) {
            log.error("Error while saving program", e);
        }
    }

    @Test
    void createProgramWithExistingName(){
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);
            assertEquals(program.getName(), foundProgram.getName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"    Design Thinking", "Data Science      "})
    void createProgramWithSpacesInProgramName(String programName){
        try{
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail()
            );

            program.setOrganizationId(foundOrganization.getId());
            program.setName(programName);
            program.setCreatedBy(userIdentity.getCreatedBy());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(programName.trim(), savedProgram.getName());

            programOutputPort.deleteProgram(savedProgram.getId());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

    @Test
    void createProgramWithNullName(){
        program.setName(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((program)));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidName(String name){
        program.setName(name);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @Test
    void createProgramWithNullOrganizationId(){
        program.setOrganizationId(null);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram((program)));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidOrganizationId(String organizationId){
        program.setOrganizationId(organizationId);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @Test
    void createProgramWithNullCreatedBy() {
        program.setCreatedBy(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidCreatedBy(String createdBy){
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"d5bf6a6c-7102-48b2-8ce9-7cd41919f074"})
    void createProgramWithNonExistingCreatedBy(String createdBy){
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());

            program.setOrganizationId(foundOrganization.getId());
            program.setCreatedBy(createdBy);
            program.setOrganizationId(foundOrganization.getId());

            MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(program));
            assertEquals(meedlException.getMessage(), MeedlMessages.NON_EXISTING_CREATED_BY.getMessage());
        } catch (MeedlException e) {
            log.error("Error finding organization", e);
        }
    }

    @Test
    @Order(2)
    void findProgramByName() {
        try {
            assertThrows(ResourceNotFoundException.class, ()->programOutputPort.findProgramByName(designThinking.getName()));
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail());

            designThinking.setCreatedBy(userIdentity.getCreatedBy());
            designThinking.setOrganizationId(foundOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(designThinking);

            Program foundProgram = programOutputPort.findProgramByName(savedProgram.getName());
            assertNotNull(foundProgram);
            programOutputPort.deleteProgram(foundProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramByNullOrEmptyName(String name) {
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.findProgramByName(name));
        assertEquals(meedlException.getMessage(), MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"  First program", "Second program   ", "    Third program     "})
    void findProgramByNameWithSpaces(String name) {
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail());

            program.setOrganizationId(foundOrganization.getId());
            program.setCreatedBy(userIdentity.getCreatedBy());
            program.setName(name);
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            Program foundProgramByName = programOutputPort.findProgramByName(name);
            assertNotNull(foundProgramByName);
            assertEquals(foundProgramByName.getName(), program.getName());
            programOutputPort.deleteProgram(savedProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by name with spaces", e);
        }
    }

    @Test
    @Order(3)
    void findProgramById() {
        try {
            assertThrows(ResourceNotFoundException.class,
                    ()->programOutputPort.findProgramByName(program.getName()));

            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            program.setOrganizationId(foundOrganization.getId());
            program.setCreatedBy(userIdentity.getCreatedBy());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            Program foundProgram = programOutputPort.findProgramById(savedProgram.getId());

            assertNotNull(foundProgram);
            assertEquals(savedProgram.getId(), foundProgram.getId());
            programOutputPort.deleteProgram(foundProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by ID", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramWithInvalidId(String id){
        program.setId(id);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById(program.getId()));
    }

    @Test
    void findProgramWithNullProgramId(){
        program.setId(null);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById((program.getId())));
    }

    @Test
    @Order(4)
    void findAllPrograms() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail());
            assertNotNull(organization);
            designThinking.setOrganizationId(organization.getId());
            designThinking.setCreatedBy(userIdentity.getCreatedBy());
            programOutputPort.saveProgram(designThinking);

            Page<Program> foundPrograms = programOutputPort.findAllPrograms(
                    designThinking.getOrganizationId(), pageSize, pageNumber);
            List<Program> programsList = foundPrograms.toList();

            assertEquals(1, foundPrograms.getTotalElements());
            assertEquals(1, foundPrograms.getTotalPages());
            assertTrue(foundPrograms.isFirst());
            assertTrue(foundPrograms.isLast());

            assertNotNull(programsList);
            assertEquals(1, programsList.size());
            assertEquals(programsList.get(0).getName(), designThinking.getName());
            assertEquals(programsList.get(0).getDuration(), designThinking.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), designThinking.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfTrainees(), designThinking.getNumberOfTrainees());
            programOutputPort.deleteProgram(foundPrograms.getContent().get(0).getId());
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
    @Order(5)
    void deleteProgram() {
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail());
            designThinking.setCreatedBy(userIdentity.getCreatedBy());
            designThinking.setOrganizationId(foundOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(designThinking);
            assertNotNull(savedProgram);

            programOutputPort.deleteProgram(savedProgram.getId());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    ()-> programOutputPort.findProgramByName(designThinking.getName()));
            assertEquals(exception.getMessage(), (PROGRAM_NOT_FOUND.getMessage()));
        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
    }

    @AfterAll
    void tearDown()  {
        try {
            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByEmployeeId(userIdentity.getId());
            employeeIdentityOutputPort.delete(employeeIdentity.getId());
            userIdentityOutputPort.deleteUserByEmail(userIdentity.getEmail());

            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertNotNull(organization);

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organization.getId());

            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationOutputPort.deleteServiceOffering(serviceOfferingId);

            organizationOutputPort.delete(organization.getId());
            assertThrows(ResourceNotFoundException.class, ()-> organizationOutputPort.findById(organization.getId()));
        } catch (MeedlException e) {
            log.error("Error while deleting service offerings", e);
        }
    }

}