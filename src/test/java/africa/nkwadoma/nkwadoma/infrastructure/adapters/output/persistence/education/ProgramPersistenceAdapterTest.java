package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.*;
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
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private LoaneeUseCase loaneeUseCase;
    @Autowired
    private CohortUseCase cohortUseCase;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private LoanBreakdownRepository loanBreakdownRepository;
    private Cohort elites;
    private Program dataAnalytics;
    private Program dataScience;
    private OrganizationIdentity organizationIdentity;
    private UserIdentity userIdentity;
    private LoanBreakdown loanBreakdown;
    private List<LoanBreakdown> loanBreakdowns;
    private List<LoaneeLoanBreakdown> loaneeBreakdowns;
    private String userId;
    private String dataAnalyticsProgramId;
    private String dataScienceProgramId;
    private String organizationId;
    private String cohortId;
    private Loanee loanee;
    private String loaneeId;
    private String loaneeUserId;
    private String loaneeLoanDetailId;
    @Autowired
    private LoaneeLoanBreakDownRepository loaneeLoanBreakDownRepository;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    private String organizationAdminId;
    private LoaneeLoanBreakdown loaneeLoanBreakdown;
    private LoaneeLoanDetail loaneeLoanDetail;
    @Autowired
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;

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

    @Test
    void findProgramByNameWithOrganizationId() {
        try {
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataScience.getName(), organizationId));
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataAnalytics.getName(), organizationId));
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();
            dataAnalytics.setCreatedBy(userId);
            Program dataAnalyticsProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = dataAnalyticsProgram.getId();

            List<Program> foundProgram = programOutputPort.findProgramByName("data", organizationId);

            assertNotNull(foundProgram);
            assertEquals(2, foundProgram.size());
            assertEquals("Data sciences", foundProgram.get(0).getName());
            assertEquals("Data analysis", foundProgram.get(1).getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
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
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataScience.getName(), organizationId));
            assertEquals(new ArrayList<>(), programOutputPort.findProgramByName(dataAnalytics.getName(), organizationId));
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();
            dataAnalytics.setCreatedBy(userId);
            Program dataAnalyticsProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = dataAnalyticsProgram.getId();

            List<Program> foundProgram = programOutputPort.findProgramByName("ysis", organizationId);

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
        assertThrows(MeedlException.class, () -> programOutputPort.findProgramByName(name, organizationId));
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
            List<Program> foundProgramByName = programOutputPort.findProgramByName(name, organizationId);
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
        assertEquals("Please provide a valid program identification.", meedlException.getMessage());
    }
    @Test
    void findAllPrograms() {
        try {
            Page<Program> firstFoundPrograms = programOutputPort.findAllPrograms(
                    userId, pageSize, pageNumber);
            dataScience.setCreatedBy(organizationIdentity.getCreatedBy());
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            dataScienceProgramId = savedProgram.getId();

            Page<Program> foundPrograms = programOutputPort.findAllPrograms(
                    userId, pageSize, pageNumber);
            List<Program> programsList = foundPrograms.toList();

            log.info("Found " + firstFoundPrograms.getTotalElements() + " programs");
            assertEquals(firstFoundPrograms.getTotalElements() + 1, foundPrograms.getTotalElements());
            assertEquals(1, foundPrograms.getTotalPages());
            assertTrue(foundPrograms.isFirst());
            assertTrue(foundPrograms.isLast());

            assertNotNull(programsList);
            assertEquals(firstFoundPrograms.stream().toList().size() + 1, programsList.size());
            assertEquals(programsList.get(0).getName(), dataScience.getName());
            assertEquals(programsList.get(0).getDuration(), dataScience.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), dataScience.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfLoanees(), dataScience.getNumberOfLoanees());
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
    void deleteProgram() {
        try {
            dataScience.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataScience);
            assertNotNull(savedProgram);

            programOutputPort.deleteProgram(savedProgram.getId());

            List<Program> programByName = programOutputPort.findProgramByName(dataScience.getName(), organizationId);
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
        Cohort savedCohort;
        try {
            dataAnalytics.setCreatedBy(userId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            assertNotNull(savedProgram);
            dataAnalyticsProgramId = savedProgram.getId();
            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns = List.of(loanBreakdown);
            Cohort cohort = TestData.createCohortData("Cohort test", dataScienceProgramId, organizationId,
                    loanBreakdowns, userId);

            cohort.setProgramId(dataAnalyticsProgramId);
            savedCohort = cohortOutputPort.save(cohort);
            assertNotNull(savedCohort);

            programOutputPort.deleteProgram(dataAnalyticsProgramId);
            savedCohort = cohortOutputPort.findCohort(savedCohort.getId());
            assertNull(savedCohort);
            cohortOutputPort.deleteCohort(cohort.getProgramId());
        } catch (MeedlException e) {
            log.error("Error while creating program {}", e.getMessage());
        }
    }

    @Test
    void deleteProgramThatHasLoanees() {
        try {
            dataScience.setCreatedBy(userId);
            dataScience = programOutputPort.saveProgram(dataScience);
            assertNotNull(dataScience);
            assertNotNull(dataScience.getId());
            dataScienceProgramId = dataScience.getId();

            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns = List.of(loanBreakdown);
            elites = TestData.createCohortData("Elite", dataScienceProgramId, organizationId,
                    loanBreakdowns, userId);

            elites = cohortUseCase.createCohort(elites);
            assertNotNull(elites);
            assertNotNull(elites.getId());
            cohortId = elites.getId();

            userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").
                    firstName("Adeshina").lastName("Qudus").email("qudus@example.com").
                    image("loanee-img.png").role(LOANEE).createdBy(organizationAdminId).build();
            loaneeLoanDetail = LoaneeLoanDetail.builder().
                    amountRequested(BigDecimal.valueOf(30000.00)).
                    initialDeposit(BigDecimal.valueOf(10000.00)).build();
            loanBreakdowns = loanBreakdownOutputPort.findAllByCohortId(cohortId);
            loanBreakdown = loanBreakdowns.get(0);

            loaneeLoanBreakdown = LoaneeLoanBreakdown.builder().
                    loaneeLoanBreakdownId(loanBreakdown.getLoanBreakdownId()).
                    itemAmount(loanBreakdown.getItemAmount()).
                    itemName(loanBreakdown.getItemName()).build();
            loaneeBreakdowns = List.of(loaneeLoanBreakdown);

            userIdentity.setEmail("testloanee@email.com");
            loanee = Loanee.builder().userIdentity(userIdentity).
                    loanBreakdowns(loaneeBreakdowns).
                    cohortId(cohortId).loaneeLoanDetail(loaneeLoanDetail).build();
            loanee = loaneeUseCase.addLoaneeToCohort(loanee);

            assertNotNull(loanee);
            assertNotNull(loanee.getUserIdentity());
            assertNotNull(loanee.getLoaneeLoanDetail());
            assertNotNull(loanee.getLoanBreakdowns());
            loaneeId = loanee.getId();
            loaneeUserId = loanee.getUserIdentity().getId();
            loaneeLoanDetailId = loanee.getLoaneeLoanDetail().getId();
        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
        assertThrows(MeedlException.class, ()-> programOutputPort.deleteProgram(dataScience.getId()));
    }

    @AfterAll
    void tearDown() {
        try {
            loaneeBreakdowns = loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByLoaneeId(loaneeId);
            loaneeBreakdowns.forEach(loaneeBreakdown -> {
                if (StringUtils.isNotEmpty(loaneeBreakdown.getLoaneeLoanBreakdownId())) {
                    loaneeLoanBreakDownRepository.deleteById(loaneeBreakdown.getLoaneeLoanBreakdownId());
                }
            });
            loaneeOutputPort.deleteLoanee(loaneeId);
            userIdentityOutputPort.deleteUserById(loaneeUserId);
            identityManagerOutputPort.deleteUser(loanee.getUserIdentity());
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
            loanBreakdowns.forEach(tuitionBreakdown -> {
                if (StringUtils.isNotEmpty(tuitionBreakdown.getLoanBreakdownId())) {
                    loanBreakdownRepository.deleteById(tuitionBreakdown.getLoanBreakdownId());
                }
            });
            cohortOutputPort.deleteCohort(cohortId);

            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.
                    findByCreatedBy(userId);
            employeeIdentityOutputPort.delete(employeeIdentity.getId());
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