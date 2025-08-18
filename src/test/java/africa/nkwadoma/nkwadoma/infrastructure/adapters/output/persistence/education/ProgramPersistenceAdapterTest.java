package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
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
class ProgramPersistenceAdapterTest {
    private final int pageSize = 10;
    private final int pageNumber = 0;
    private final String testId = "466e11ca-d6c3-4bf1-b226-e2ed3fab6788";
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private Program dataAnalytics;
    private Program dataScience;
    private OrganizationIdentity organizationIdentity;
    private UserIdentity userIdentity;
    private String userId;
    private String dataAnalyticsProgramId;
    private String dataScienceProgramId;
    private String organizationId;
    private String organizationAdminId;


    @BeforeAll
    void init() {
        userIdentity = TestData.createTestUserIdentity("worktest@email.com", "8c5b64dc-e2c6-4b12-868b-344faeeec45d");

        try {
            organizationIdentity = TestData.createOrganizationTestData(
                    "Brown Hill institute", "RC3499987",
                    List.of(OrganizationEmployeeIdentity.builder().
                            meedlUser(userIdentity).build()));

            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            organizationId = savedOrganization.getId();
            userIdentity.setId("c533121e-565a-45ce-829b-6d24c7eeef14");
            userId = userIdentityOutputPort.save(userIdentity).getId();
            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
            employeeIdentity.setOrganization(organizationId);
            OrganizationEmployeeIdentity organizationEmployeeIdentity = employeeIdentityOutputPort.save(employeeIdentity);
            organizationAdminId = organizationEmployeeIdentity.getMeedlUser().getId();
        } catch (MeedlException e) {
            log.error("Error creating organization", e);
        }
    }

    @BeforeEach
    void setUp() {
        dataAnalytics = new Program();
        dataAnalytics.setName("Data analysis");
        dataAnalytics.setProgramDescription("A rigorous course in the art and science of Data analysis");
        dataAnalytics.setMode(ProgramMode.FULL_TIME);
        dataAnalytics.setProgramStatus(ActivationStatus.ACTIVE);
        dataAnalytics.setDuration(2);
        dataAnalytics.setOrganizationIdentity(organizationIdentity);
        dataAnalytics.setDeliveryType(DeliveryType.ONSITE);
        dataAnalytics.setDurationType(DurationType.MONTHS);

        dataScience = new Program();
        dataScience.setName("Data sciences");
        dataScience.setProgramDescription("The art of putting thought into solving problems");
        dataScience.setMode(ProgramMode.FULL_TIME);
        dataScience.setProgramStatus(ActivationStatus.ACTIVE);
        dataScience.setOrganizationIdentity(organizationIdentity);
        dataScience.setDuration(1);
        dataScience.setDeliveryType(DeliveryType.ONSITE);
        dataScience.setDurationType(DurationType.YEARS);
    }


    @Order(1)
    @Test
    void saveProgram() {
        Program savedProgram = null;
        try {
            dataAnalytics.setCreatedBy(userId);
            savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
        assertNotNull(savedProgram);
        assertNotNull(savedProgram.getId());
        assertEquals(dataAnalytics.getName(), savedProgram.getName());
        assertEquals(dataAnalytics.getProgramStatus(), savedProgram.getProgramStatus());
        assertEquals(dataAnalytics.getProgramDescription(), savedProgram.getProgramDescription());
        assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
    }

    @Order(2)
    @Test
    void saveAnotherProgram() {
        Program savedProgram = null;
        try {
            dataScience.setCreatedBy(userId);
            savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();

        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
        assertNotNull(savedProgram);
        assertNotNull(savedProgram.getId());
        assertEquals(dataScience.getName(), savedProgram.getName());
        assertEquals(dataScience.getProgramStatus(), savedProgram.getProgramStatus());
        assertEquals(dataScience.getProgramDescription(), savedProgram.getProgramDescription());
        assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
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
            dataScience.setOrganizationIdentity(savedOrganization);
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

    @Order(3)
    @Test
    void findProgramByNameWithOrganizationId() {
        Page<Program> foundProgram = Page.empty();
        try {
            Program searchQuery = Program.builder().name("data").pageSize(pageSize).pageNumber(pageNumber).build();
            foundProgram = programOutputPort.findProgramByNameWithinOrganization(searchQuery, organizationId);
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
        assertNotNull(foundProgram);
        assertEquals(2, foundProgram.getContent().size());
        assertEquals("Data analysis", foundProgram.getContent().get(0).getName());
        assertEquals("Data sciences", foundProgram.getContent().get(1).getName());
    }

    @Test
    @Order(4)
    void findProgramByName() {
        Page<Program> foundProgram = Page.empty();

        try {
            foundProgram = programOutputPort.findProgramByName("data",pageNumber,pageSize);
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
        assertNotNull(foundProgram);
        assertEquals(2, foundProgram.getContent().size());
        assertEquals("Data analysis", foundProgram.getContent().get(0).getName());
        assertEquals("Data sciences", foundProgram.getContent().get(1).getName());
    }

    @Test
    @Order(5)
    void findProgramByNameThatMatchesOneResult() {
        Page<Program> foundProgram = Page.empty();
        try {
            foundProgram = programOutputPort.findProgramByName("ysis", pageNumber,pageSize);

        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
        assertNotNull(foundProgram);
        assertEquals(1, foundProgram.getContent().size());
        assertEquals("Data analysis", foundProgram.getContent().get(0).getName());
    }

    @Test
    void findProgramByNullOrganizationId() {
        assertThrows(MeedlException.class, () -> programOutputPort.findProgramByNameWithinOrganization(dataAnalytics, null));
    }


    @Order(6)
    @Test
    void findProgramById() {
        Program foundProgram = null;
        try {
            foundProgram = programOutputPort.findProgramById(dataAnalyticsProgramId);
        } catch (MeedlException e) {
            log.error("Error finding program by ID", e);
        }
        assertNotNull(foundProgram);
        assertEquals(dataAnalyticsProgramId, foundProgram.getId());
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
        assertEquals("Please provide a valid program identification.", meedlException.getMessage());
    }

    @Order(7)
    @Test
    void findAllPrograms() {
        Page<Program> foundPrograms = Page.empty();
        try {

            foundPrograms = programOutputPort.findAllPrograms(
                    userId, pageSize, pageNumber);

        } catch (MeedlException e) {
            log.error("Error finding all programs", e);
        }
        assertNotNull(foundPrograms);
        assertEquals(foundPrograms.getContent().size(), 2);
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void findAllProgramsByEmptyOrganizationId(String organizationId) {
        assertThrows(MeedlException.class, () -> programOutputPort.findAllPrograms(organizationId, pageSize, pageNumber));
    }

    @Order(8)
    @Test
    void deleteProgram() {
        Program program = new Program();
        try {
            programOutputPort.deleteProgram(dataScience.getId());
             program = programOutputPort.findProgramById(dataScience.getId());
        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
        assertNull(program.getId());
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


    @AfterAll
    void tearDown() {
        try {
            programOutputPort.deleteProgram(dataAnalyticsProgramId);
            userIdentityOutputPort.deleteUserById(userId);
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
            log.error("Error while cleaning up", e);
        }
    }

}