package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
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
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Autowired
    private CohortRepository cohortRepository;
    @Autowired
    private CohortMapper cohortMapper;
    private Cohort elites;
    private Program program;
    private Program designThinking;
    private OrganizationIdentity organizationIdentity;
    private UserIdentity userIdentity;
    private final int pageSize = 10;
    private final int pageNumber = 0;
    private final String testId = "81d45178-9b05-4f35-8d96-5759f9fc5ea7";;

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

        elites = new Cohort();
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        elites.setName("Elite");
        elites.setCreatedBy(userIdentity.getCreatedBy());

        userIdentity = new UserIdentity();
        userIdentity.setFirstName("Joel");
        userIdentity.setLastName("Jacobs");
        userIdentity.setEmail("joel@johnson.com");
        userIdentity.setPhoneNumber("098647748393");
        userIdentity.setId(testId);
        userIdentity.setCreatedBy(testId);
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity.setRole(PORTFOLIO_MANAGER);
    }
    @BeforeAll
    void init() {
        try {
            OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().
                    meedlUser(userIdentity).build();
            organizationIdentity = new OrganizationIdentity();
            organizationIdentity.setName("Amazing Grace Enterprises");
            organizationIdentity.setEmail("rachel@gmail.com");
            organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
            organizationIdentity.setRcNumber("RC345677");
            organizationIdentity.setId(testId);
            organizationIdentity.setPhoneNumber("0907658483");
            organizationIdentity.setTin("Tin5678");
            organizationIdentity.setNumberOfPrograms(0);
            organizationIdentity.setCreatedBy("3a6d1124-1349-4f5b-831a-ac269369a90f");
            ServiceOffering serviceOffering = new ServiceOffering();
            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
            serviceOffering.setIndustry(Industry.EDUCATION);
            organizationIdentity.setServiceOfferings(List.of(serviceOffering));
            organizationIdentity.setWebsiteAddress("webaddress.org");
//            organizationIdentity.setOrganizationEmployees(List.of(employeeIdentity));

            userIdentity = new UserIdentity();
            userIdentity.setFirstName("Joel");
            userIdentity.setLastName("Jacobs");
            userIdentity.setEmail("joel@johnson.com");
            userIdentity.setId(organizationIdentity.getCreatedBy());
            userIdentity.setPhoneNumber("098647748393");
            userIdentity.setEmailVerified(true);
            userIdentity.setEnabled(true);
            userIdentity.setCreatedAt(LocalDateTime.now().toString());
            userIdentity.setRole(PORTFOLIO_MANAGER);
            userIdentity.setCreatedBy(organizationIdentity.getCreatedBy());

            organizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
                    meedlUser(userIdentity).build()));
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
            program.setCreatedBy(foundOrganization.getCreatedBy());
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
            OrganizationIdentity foundOrganizationIdentity = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(foundOrganizationIdentity
                            .getId());
            ServiceOffering serviceOffering = organizationServiceOfferings.get(0).getServiceOffering();
            serviceOffering.setName("NON_TRAINING");
            serviceOffering.setIndustry(Industry.BANKING);

            foundOrganizationIdentity.setServiceOfferings(List.of(serviceOffering));
            UserIdentity foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            foundUserIdentity.setCreatedBy(foundOrganizationIdentity.getCreatedBy());
            foundUserIdentity.setId(foundOrganizationIdentity.getCreatedBy());

            foundOrganizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
                    meedlUser(foundUserIdentity).build()));
            OrganizationIdentity savedOrganization = organizationOutputPort.save(foundOrganizationIdentity);

            userIdentityOutputPort.save(foundUserIdentity);
            assertNotNull(savedOrganization);

            designThinking.setOrganizationId(savedOrganization.getId());
            designThinking.setCreatedBy(foundUserIdentity.getCreatedBy());

            assertThrows(MeedlException.class, ()-> programOutputPort.saveProgram(designThinking));

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
    @ValueSource(strings = {"    Electrical Engineering", "Data Science      "})
    void createProgramWithSpacesInProgramName(String programName){
        try{
            OrganizationIdentity foundOrganizationIdentity = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(foundOrganizationIdentity
                            .getId());
            ServiceOffering serviceOffering = organizationServiceOfferings.get(0).getServiceOffering();
            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
            serviceOffering.setIndustry(Industry.EDUCATION);

            foundOrganizationIdentity.setServiceOfferings(List.of(serviceOffering));
            UserIdentity foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            foundUserIdentity.setCreatedBy(foundOrganizationIdentity.getCreatedBy());
            foundUserIdentity.setId(foundOrganizationIdentity.getCreatedBy());

            foundOrganizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
                    meedlUser(foundUserIdentity).build()));
            OrganizationIdentity savedOrganization = organizationOutputPort.save(foundOrganizationIdentity);

            userIdentityOutputPort.save(foundUserIdentity);
            assertNotNull(savedOrganization);

            program.setOrganizationId(foundOrganizationIdentity.getId());
            program.setName(programName);
            program.setCreatedBy(organizationIdentity.getCreatedBy());
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

            designThinking.setCreatedBy(organizationIdentity.getCreatedBy());
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
            program.setCreatedBy(organizationIdentity.getCreatedBy());
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
            program.setCreatedBy(organizationIdentity.getCreatedBy());
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
    void findProgramByEmptyId(String id){
        program.setId(id);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById(program.getId()));
    }

    @Test
    void findProgramWithNullProgramId(){
        program.setId(null);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById((program.getId())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"non-uuid", "3657679"})
    void viewProgramWithNonUUIDId(String programId) {
        program.setId(programId);
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.findProgramById(programId));
        assertEquals(meedlException.getMessage(), MeedlMessages.UUID_NOT_VALID.getMessage());
    }

    @Test
    @Order(4)
    void findAllPrograms() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(
                    organizationIdentity.getEmail());
            assertNotNull(organization);
            designThinking.setOrganizationId(organization.getId());
            designThinking.setCreatedBy(organizationIdentity.getCreatedBy());
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
            designThinking.setCreatedBy(organizationIdentity.getCreatedBy());
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

    @Test
    void deleteNonExistingProgram() {
        program.setId("1de71eaa-de6d-4cdf-8f93-aa7be533f4aa");
        assertThrows(ResourceNotFoundException.class, ()->programOutputPort.deleteProgram(program.getId()));
    }

    @Test
    void deleteProgramWithNullId() {
        program.setId(null);
        assertThrows(MeedlException.class, ()->programOutputPort.deleteProgram(program.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"009837", "non-uuid"})
    void deleteProgramWithNonUUID(String programId) {
        assertThrows(MeedlException.class, () -> programOutputPort.deleteProgram(programId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteProgramByEmptyId(String id) {
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.deleteProgram(id));
        assertEquals(meedlException.getMessage(), MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
    }

    @Test
    void deleteProgramWithCohort() {
        Program savedProgram = new Program();
        CohortEntity savedCohort = new CohortEntity();
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            program.setOrganizationId(foundOrganization.getId());
            program.setCreatedBy(userIdentity.getCreatedBy());
            savedProgram = programOutputPort.saveProgram(program);
            assertNotNull(savedProgram);

            elites.setProgramId(savedProgram.getId());

            savedCohort = cohortRepository.save(cohortMapper.toCohortEntity(elites));
            assertNotNull(savedCohort);
        } catch (MeedlException e) {
            log.error("Error while creating program {}", e.getMessage());
        }
        //TODO this test needs to be fixed
//        String id = savedProgram.getId();
//        assertThrows(EducationException.class, ()->programOutputPort.deleteProgram(id));

        cohortRepository.delete(savedCohort);
        try {
            programOutputPort.deleteProgram(savedProgram.getId());
        } catch (MeedlException e) {
            log.error("Error deleting program {}", e.getMessage());
        }
    }

    @AfterAll
    void tearDown()  {
        try {
            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.
                    findByCreatedBy(organizationIdentity.getCreatedBy());
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