package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
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

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.*;
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
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private ProgramRepository programRepository;
    private Cohort elites;
    private Program dataAnalytics;
    private Program dataScience;
    private OrganizationIdentity organizationIdentity;
    private UserIdentity userIdentity;
    private String userId;
    private String dataAnalyticsProgramId;
    private String dataScienceProgramId;
    private String organizationId;
    private final int pageSize = 10;
    private final int pageNumber = 0;
    private final String testId = "466e11ca-d6c3-4bf1-b226-e2ed3fab6788";

    @BeforeEach
    void setUp() {
        dataAnalytics = new Program();
        dataAnalytics.setName("Data analysis");
        dataAnalytics.setProgramDescription("A rigorous course in the art and science of Data analysis");
        dataAnalytics.setMode(ProgramMode.FULL_TIME);
        dataAnalytics.setProgramStatus(ActivationStatus.ACTIVE);
        dataAnalytics.setDuration(2);
        dataAnalytics.setDeliveryType(DeliveryType.ONSITE);
        dataAnalytics.setDurationType(DurationType.MONTHS);

        dataScience = new Program();
        dataScience.setName("Data sciences");
        dataScience.setProgramDescription("The art of putting thought into solving problems");
        dataScience.setMode(ProgramMode.FULL_TIME);
        dataScience.setProgramStatus(ActivationStatus.ACTIVE);
        dataScience.setDuration(1);
        dataScience.setDeliveryType(DeliveryType.ONSITE);
        dataScience.setDurationType(DurationType.YEARS);

        elites = new Cohort();
        elites.setStartDate(LocalDate.of(2024, 10, 18));
        elites.setName("Elites");
        elites.setCreatedBy(userIdentity.getCreatedBy());
    }

    @BeforeAll
    void init() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("Joel");
        userIdentity.setLastName("Jacobs");
        userIdentity.setEmail("joel0949@johnson.com");
        userIdentity.setPhoneNumber("098647748393");
        userIdentity.setId(testId);
        userIdentity.setCreatedBy(testId);
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity.setRole(PORTFOLIO_MANAGER);
        try {
            organizationIdentity = new OrganizationIdentity();
            organizationIdentity.setName("Brown Hill institute");
            organizationIdentity.setEmail("rachel423@gmail.com");
            organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
            organizationIdentity.setRcNumber("RC3499987");
            organizationIdentity.setId(testId);
            organizationIdentity.setPhoneNumber("0907658483");
            organizationIdentity.setTin("Tin8675678");
            organizationIdentity.setNumberOfPrograms(0);
            organizationIdentity.setCreatedBy(testId);
            ServiceOffering serviceOffering = new ServiceOffering();
            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
            serviceOffering.setIndustry(Industry.EDUCATION);
            organizationIdentity.setServiceOfferings(List.of(serviceOffering));
            organizationIdentity.setWebsiteAddress("webaddress.org");
            organizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
                    meedlUser(userIdentity).build()));

            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            organizationId = savedOrganization.getId();
            userId = userIdentityOutputPort.save(userIdentity).getId();
            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
            employeeIdentity.setOrganization(organizationId);
            organizationIdentity.getOrganizationEmployees().forEach(
                    organizationEmployeeIdentity -> employeeIdentityOutputPort.save(employeeIdentity));
        } catch (MeedlException e) {
            log.error("Error creating organization", e);
        }
    }

    @AfterEach
    void cleanUp() {
        if (StringUtils.isNotEmpty(dataAnalyticsProgramId)) {
            programRepository.deleteById(dataAnalyticsProgramId);
        }
        if (StringUtils.isNotEmpty(dataScienceProgramId)) {
            programRepository.deleteById(dataScienceProgramId);
        }
    }

    @Test
    void saveProgram() {
        try {
            dataAnalytics.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(dataAnalytics.getName(), savedProgram.getName());
            assertEquals(dataAnalytics.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(dataAnalytics.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

    @Test
    void createProgramWithNullProgram() {
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((null)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"    Electrical Engineering", "Cloud Computing      "})
    void createProgramWithSpacesInProgramName(String programName) {
        try {
            dataAnalytics.setName(programName);
            dataAnalytics.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(programName.trim(), savedProgram.getName());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
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

            dataScience.setCreatedBy(foundUserIdentity.getCreatedBy());

            assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(dataScience));
        } catch (MeedlException e) {
            log.error("Error while saving program", e);
        }
    }

    @Test
    void createProgramWithNullName() {
        dataAnalytics.setName(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((dataAnalytics)));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "121323", "#ndj", "(*^#()@", "Haus*&^"})
    void createProgramWithInvalidName(String programName) {
        dataAnalytics.setName(programName);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(dataAnalytics));
    }

    @Test
    void createProgramWithNullCreatedBy() {
        dataAnalytics.setCreatedBy(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(dataAnalytics));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidCreatedBy(String createdBy) {
        dataAnalytics.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(dataAnalytics));
    }

    @Test
    void createProgramWithNonExistingCreatedBy() {
        dataAnalytics.setCreatedBy("f2a25ed8-a594-4cb4-a2fb-8e0dcca72f71");
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(dataAnalytics));
        assertEquals(meedlException.getMessage(), MeedlMessages.NON_EXISTING_CREATED_BY.getMessage());
    }

    @Test
    void findProgramByName() {
        try {
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataScience.getName()));
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataAnalytics.getName()));
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();
            dataAnalytics.setCreatedBy(userId);
            Program dataAnalyticsProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = dataAnalyticsProgram.getId();

            List<Program> foundProgram = programOutputPort.findProgramByName("data");

            assertNotNull(foundProgram);
            assertEquals(2, foundProgram.size());
            assertEquals("Data sciences", foundProgram.get(0).getName());
            assertEquals("Data analysis", foundProgram.get(1).getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
    }

    @Test
    void findProgramByNameThatMatchesOneResult() {
        try {
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataScience.getName()));
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataAnalytics.getName()));
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();
            dataAnalytics.setCreatedBy(userId);
            Program dataAnalyticsProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = dataAnalyticsProgram.getId();

            List<Program> foundProgram = programOutputPort.findProgramByName("ysis");

            assertNotNull(foundProgram);
            assertEquals(1, foundProgram.size());
            assertEquals("Data analysis", foundProgram.get(0).getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramByNullOrEmptyName(String name) {
        assertThrows(MeedlException.class, () -> programOutputPort.findProgramByName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  First program", "Second program   ", "    Third program     "})
    void findProgramByNameWithSpaces(String name) {
        try {
            dataAnalytics.setCreatedBy(userId);
            dataAnalytics.setName(name);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

            assertNotNull(savedProgram);
            List<Program> foundProgramByName = programOutputPort.findProgramByName(name);
            assertNotNull(foundProgramByName);
            assertEquals(foundProgramByName.get(0).getName(), dataAnalytics.getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name with spaces", e);
        }
    }

    @Test
    void findProgramById() {
        try {
            dataAnalytics.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            assertNotNull(savedProgram);
            dataAnalyticsProgramId = savedProgram.getId();

            Program foundProgram = programOutputPort.findProgramById(savedProgram.getId());

            assertNotNull(foundProgram);
            assertEquals(savedProgram.getId(), foundProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by ID", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramByEmptyId(String id) {
        dataAnalytics.setId(id);
        assertThrows(MeedlException.class, () -> programOutputPort.findProgramById(dataAnalytics.getId()));
    }

    @Test
    void findProgramWithNullProgramId() {
        dataAnalytics.setId(null);
        assertThrows(MeedlException.class, () -> programOutputPort.findProgramById((dataAnalytics.getId())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"non-uuid", "3657679"})
    void viewProgramWithNonUUIDId(String programId) {
        dataAnalytics.setId(programId);
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.findProgramById(programId));
        assertEquals(meedlException.getMessage(), MeedlMessages.UUID_NOT_VALID.getMessage());
    }

    @Test
    void findAllPrograms() {
        try {
            dataScience.setCreatedBy(organizationIdentity.getCreatedBy());
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();

            Page<Program> foundPrograms = programOutputPort.findAllPrograms(
                    userId, pageSize, pageNumber);
            List<Program> programsList = foundPrograms.toList();

            assertEquals(1, foundPrograms.getTotalElements());
            assertEquals(1, foundPrograms.getTotalPages());
            assertTrue(foundPrograms.isFirst());
            assertTrue(foundPrograms.isLast());

            assertNotNull(programsList);
            assertEquals(1, programsList.size());
            assertEquals(programsList.get(0).getName(), dataScience.getName());
            assertEquals(programsList.get(0).getDuration(), dataScience.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), dataScience.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfTrainees(), dataScience.getNumberOfTrainees());
        } catch (MeedlException e) {
            log.error("Error finding all programs", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void findAllProgramsByEmptyOrganizationId(String organizationId) {
        assertThrows(MeedlException.class, () -> programOutputPort.findAllPrograms(organizationId, pageSize, pageNumber));
    }

    @Test
//    @Order(5)
    void deleteProgram() {
        try {
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            assertNotNull(savedProgram);

            programOutputPort.deleteProgram(savedProgram.getId());

            List<Program> programByName = programOutputPort.findProgramByName(dataScience.getName());
            assertTrue(programByName.isEmpty());
        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
    }

    @Test
    void deleteNonExistingProgram() {
        dataAnalytics.setId("1de71eaa-de6d-4cdf-8f93-aa7be533f4aa");
        assertThrows(ResourceNotFoundException.class, () -> programOutputPort.deleteProgram(dataAnalytics.getId()));
    }

    @Test
    void deleteProgramWithNullId() {
        dataAnalytics.setId(null);
        assertThrows(MeedlException.class, () -> programOutputPort.deleteProgram(dataAnalytics.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"009837", "non-uuid"})
    void deleteProgramWithNonUUID(String programId) {
        assertThrows(MeedlException.class, () -> programOutputPort.deleteProgram(programId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteProgramByEmptyId(String id) {
        assertThrows(MeedlException.class, () -> programOutputPort.deleteProgram(id));
    }

    @Test
    void deleteProgramWithCohort() {
        Program savedProgram;
        Cohort savedCohort;
        try {
            dataAnalytics.setCreatedBy(userId);
            savedProgram = programOutputPort.saveProgram(dataAnalytics);
            assertNotNull(savedProgram);
            dataAnalyticsProgramId = savedProgram.getId();

            elites.setProgramId(dataAnalyticsProgramId);
            savedCohort = cohortOutputPort.save(elites);
            assertNotNull(savedCohort);

            programOutputPort.deleteProgram(dataAnalyticsProgramId);
            Cohort cohort = cohortOutputPort.findCohort(savedCohort.getId());
            assertNull(cohort);
        } catch (MeedlException e) {
            log.error("Error while creating program {}", e.getMessage());
        }
    }

    @AfterAll
    void tearDown() {
        try {
            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.
                    findByCreatedBy(userId);
            employeeIdentityOutputPort.delete(employeeIdentity.getId());
            userIdentityOutputPort.deleteUserByEmail(userIdentity.getEmail());

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organizationId);

            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationOutputPort.deleteServiceOffering(serviceOfferingId);

            organizationOutputPort.delete(organizationId);
            assertThrows(ResourceNotFoundException.class, () -> organizationOutputPort.findById(organizationId));
        } catch (MeedlException e) {
            log.error("Error while deleting service offerings", e);
        }
    }

}