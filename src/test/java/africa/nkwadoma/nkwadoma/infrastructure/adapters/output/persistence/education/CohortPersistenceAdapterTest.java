package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortPersistenceAdapterTest {

    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;
    private Cohort xplorers;
    @Autowired
    private CohortRepository cohortRepository;
    private String meedleUserId;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private  OrganizationEmployeeIdentity employeeIdentity;
    private  OrganizationIdentity organizationIdentity;
    @Autowired
    private CreateOrganizationUseCase organizationUseCase;
    @Autowired
    private ProgramCohortOutputPort programCohortOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagementOutputPort;
    private Program program;
    private String cohortOneId;
    private String programId;
    private String cohortTwoId;
    private String organizationId;

    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder().firstName("Fred 20").role(IdentityRole.valueOf("PORTFOLIO_MANAGER")).
                lastName("Benson Ayo").email("fred2110@example.com").createdBy("8937-b9897g3-bv38").build();
        employeeIdentity = OrganizationEmployeeIdentity.builder()
                .meedlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder().email("org1@example.com").
                name("My Organization21 Test").rcNumber("56767").serviceOfferings(
                        List.of(ServiceOffering.builder().industry(Industry.EDUCATION).name(ServiceOfferingType.TRAINING.name()).build())).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        program = Program.builder().name("My program Test").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).
                createdAt(LocalDateTime.now()).programStartDate(LocalDate.now()).build();
        try {
            organizationIdentity = organizationUseCase.inviteOrganization(organizationIdentity);
            organizationId = organizationIdentity.getId();
            meedleUserId = organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getId();
            program.setOrganizationId(organizationIdentity.getId());
            program.setCreatedBy(meedleUserId);
            program = programOutputPort.saveProgram(program);
            programId = program.getId();
        } catch (MeedlException e) {
            log.info("Failed to save program {}", e.getMessage());
        }
    }


    @BeforeEach
    public void setUp(){
        log.info("progam id is --- {}", program.getId());
        LoanBreakdown loanBreakdown = new LoanBreakdown();
        loanBreakdown.setCurrency("USD");
        loanBreakdown.setItemAmount(new BigDecimal("50000"));
        loanBreakdown.setItemName("Loan Break");

        elites = new Cohort();
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        elites.setProgramId(program.getId());
        elites.setName("Elite ");
        elites.setCreatedBy(meedleUserId);
        elites.setLoanBreakdowns(List.of(loanBreakdown));

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        xplorers.setProgramId(programId);
        xplorers.setCreatedBy(meedleUserId);
        xplorers.setLoanBreakdowns(List.of(loanBreakdown));
    }


    @Test
    void saveCohortWithNullCohort(){
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(null));
    }
    @Test
    void saveCohortWithNullProgramId(){
        elites.setProgramId(null);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithValidProgramId(String programId){
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithEmptyName(String name){
        elites.setName(name);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " ", "email@gmail.com","3gdgttebdindndd673ydieyendjdljdh"})
    void saveCohortWithInvalidCreator(String createdBy){
        elites.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }

    @Order(1)
    @Test
    void saveCohort() {
        try {
            Cohort cohort = cohortOutputPort.saveCohort(elites);
            assertNotNull(cohort);
            assertNotNull(cohort.getId());
            cohortOneId = cohort.getId();
            assertEquals(cohort.getName(), elites.getName());
            assertNotNull(elites.getLoanBreakdowns());
            assertNotNull(elites.getLoanBreakdowns().get(0));
            assertEquals(elites.getLoanBreakdowns().get(0).getItemName(), cohort.getLoanBreakdowns().get(0).getItemName());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(2)
    @Test
    void saveCohortWithExistingCohortName() {
        elites.setId(meedleUserId);
        assertThrows(MeedlException.class,() ->cohortOutputPort.saveCohort(elites));
    }


    @Order(3)
    @Test
    void saveAnotherCohortInProgram() {
        try {
            Cohort cohort = cohortOutputPort.saveCohort(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            assertEquals(ActivationStatus.ACTIVE.name(),cohort.getActivationStatus().name());
            assertEquals(CohortStatus.CURRENT.name(),cohort.getCohortStatus().name());
            cohortTwoId = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(4)
    @Test
    void viewCohortDetails(){
        Cohort viewedCohort = new Cohort() ;
        try{
            viewedCohort = cohortOutputPort.viewCohortDetails(meedleUserId, programId, cohortTwoId);
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(viewedCohort.getName(),xplorers.getName());
        assertEquals(viewedCohort.getCreatedBy(),xplorers.getCreatedBy());
    }

    @Order(5)
    @Test
    void viewCohortWithNullUserId(){
        assertThrows(MeedlException.class, () -> cohortOutputPort.viewCohortDetails(null,
                program.getId(),
                cohortTwoId));
    }

    @Test
    void viewCohortWithNullProgramId(){
        assertThrows(MeedlException.class, () -> cohortOutputPort.viewCohortDetails(elites.getCreatedBy(),
                null,
                cohortTwoId));
    }

    @Test
    void viewCohortWithNullCohortId(){
        assertThrows(MeedlException.class, () -> cohortOutputPort.viewCohortDetails(elites.getCreatedBy(),
                program.getId(),
                null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyUserId(String userId){
        assertThrows(MeedlException.class, ()->
                cohortOutputPort.viewCohortDetails(userId,
                        program.getId(),
                        cohortTwoId));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyProgramId(String programId){
        assertThrows(MeedlException.class, ()->
                cohortOutputPort.viewCohortDetails(elites.getCreatedBy(),
                        programId,
                        cohortTwoId));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyCohortId(String cohortId){
        assertThrows(MeedlException.class, ()->
                cohortOutputPort.viewCohortDetails(elites.getCreatedBy(),
                        program.getId(),
                        cohortId));
    }
@Order(6)
@Test
void deleteCohort(){
        Optional<CohortEntity> foundCohort = cohortRepository.findById(cohortOneId);
        assertTrue(foundCohort.isPresent());
    try {
        cohortOutputPort.deleteCohort(cohortOneId);
    } catch (MeedlException e) {
        throw new RuntimeException(e);
    }
    foundCohort = cohortRepository.findById(cohortOneId);
        assertFalse(foundCohort.isPresent());
}
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE, "ndjnhfd,"})
    void deleteCohortWithInvalidId(String cohortId){
        assertThrows(MeedlException.class, ()-> cohortOutputPort.deleteCohort(cohortId));
    }

    @Order(7)
    @Test
    void cleanUp() throws MeedlException {
//        log.info("{} {} {} {} ",meedleUserId, programId, cohortTwoId, organizationId);
        identityManagementOutputPort.deleteClient(organizationId);
        identityManagementOutputPort.deleteUser(UserIdentity.builder().id(meedleUserId).build());
        programOutputPort.deleteProgram(programId);
        cohortOutputPort.deleteCohort(cohortTwoId);
        programCohortOutputPort.delete(programId);
        organizationIdentityOutputPort.delete(organizationId);
        userIdentityOutputPort.deleteUserById(meedleUserId);
        cohortRepository.deleteById(cohortTwoId);
        cohortRepository.deleteById(cohortOneId);
    }
}
