package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
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
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CohortPersistenceAdapterTest {

    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;
    private Cohort xplorers;
    private Cohort cohort;
    @Autowired
    private CohortRepository cohortRepository;
    private String meedleUserId;
    private String meedleUser;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagementOutputPort;
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
    private Program program;
    private String programId;
    private String cohortOneId;
    private String cohortTwoId;
    private String organizationId;
    private LoanBreakdown loanBreakdown;


    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder()
                .firstName("Ford").role(IdentityRole.PORTFOLIO_MANAGER).
                lastName("Benson").email("freddy102@example.com").createdBy("61fb3beb-f200-4b16-ac58-c28d737b546c").build();
        employeeIdentity = OrganizationEmployeeIdentity.builder()
                .meedlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder().email("fordorganization012@example.com")
                .name("Organization09 Ford").rcNumber("7576").serviceOfferings(
                        List.of(ServiceOffering.builder().industry(Industry.EDUCATION).name(ServiceOfferingType.TRAINING.name()).build())).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity))
                .build();

        program = Program.builder().name("My program Ford").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).
                createdAt(LocalDateTime.now()).programStartDate(LocalDate.now()).build();
        try {
            organizationIdentity = organizationUseCase.inviteOrganization(organizationIdentity);
            log.info("Organization identity saved before program {}",organizationIdentity);
            organizationId = organizationIdentity.getId();
            meedleUserId = organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getId();
            program.setOrganizationId(organizationIdentity.getId());
            program.setCreatedBy(meedleUserId);
            program = programOutputPort.saveProgram(program);
            log.info("Program saved {}",program);
            programId = program.getId();
        } catch (MeedlException e) {
            log.info("Failed to save program {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setUp(){
        log.info("progam id is --- {}", program.getId());
        loanBreakdown = new LoanBreakdown();
        loanBreakdown.setCurrency("USD");
        loanBreakdown.setItemAmount(new BigDecimal("50000"));
        loanBreakdown.setItemName("Loan Break");

        elites = new Cohort();
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        elites.setProgramId(program.getId());
        elites.setName("Elite");
        elites.setCreatedBy(meedleUserId);
        elites.setLoanBreakdowns(List.of(loanBreakdown));

        CohortLoanDetail cohortLoanDetail = getCohortLoanDetail();

        elites.setCohortLoanDetail(cohortLoanDetail);

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        xplorers.setProgramId(programId);
        xplorers.setCreatedBy(meedleUserId);
        xplorers.setLoanBreakdowns(List.of(loanBreakdown));
    }

    private static CohortLoanDetail getCohortLoanDetail() {
        CohortLoanDetail cohortLoanDetail = new CohortLoanDetail();
        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setDebtPercentage(0.34);
        loanDetail.setRepaymentPercentage(0.67);
        loanDetail.setMonthlyExpected(BigDecimal.valueOf(450));
        loanDetail.setTotalAmountRepaid(BigDecimal.valueOf(500));
        loanDetail.setTotalInterestIncurred(BigDecimal.valueOf(600));
        loanDetail.setLastMonthActual(BigDecimal.valueOf(200));
        loanDetail.setTotalAmountDisbursed(BigDecimal.valueOf(50000));
        loanDetail.setTotalOutstanding(BigDecimal.valueOf(450));
        cohortLoanDetail.setLoanDetail(loanDetail);
        return cohortLoanDetail;
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
        //TODO validate for UUID
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
            log.info("{} {} {}", meedleUserId,programId, cohortTwoId );
            viewedCohort = cohortOutputPort.viewCohortDetails(meedleUserId, programId, cohortTwoId);
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(xplorers.getName(), viewedCohort.getName());
        assertEquals(xplorers.getCreatedBy(), viewedCohort.getCreatedBy());
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

    @Test
    void viewAllCohortInAProgram(){
        List<Cohort> cohorts = new ArrayList<>();
        try{
            cohorts = cohortOutputPort.findAllCohortInAProgram(program.getId());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(2,cohorts.size());

    }
    
    @Order(6)
    @Test
    void searchForCohort(){
        Cohort searchedCohort = new Cohort();
        try{
            searchedCohort =
                    cohortOutputPort.searchForCohortInAProgram(elites.getName(),elites.getProgramId());
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
       assertEquals(searchedCohort.getName(),elites.getName());
       assertEquals(searchedCohort.getProgramId(),elites.getProgramId());
    }
    @Order(7)
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

    @ParameterizedTest
    @ValueSource(strings= {"wrong cohort 1", "wrong cohort 2"})
    void searchForCohortWithWrongCohortName(String cohortName){
        assertThrows(MeedlException.class, ()->
                     cohortOutputPort.searchForCohortInAProgram(cohortName,elites.getProgramId()));
    }


    @Test
    void cannotEditCohortWithLoanDetails(){
        try {
            Cohort cohort = cohortOutputPort.viewCohortDetails(meedleUserId, program.getId(), cohortOneId);
            log.info("Cohort found : {}" , cohort);
            assertThrows(MeedlException.class, () -> cohortOutputPort.saveCohort(cohort));
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(8)
    @Test
    void cohortWithoutLoanDetailsCanBeEdited(){
        Cohort editedCohort = new Cohort();
        try{
            Cohort foundCohort = cohortOutputPort.viewCohortDetails(meedleUserId,program.getId(),cohortTwoId);
            foundCohort.setLoanBreakdowns(List.of(loanBreakdown));
            log.info("Found cohort============>: {}", foundCohort);
            foundCohort.setName("edited cohort");
            editedCohort = cohortOutputPort.saveCohort(foundCohort);
        } catch (MeedlException exception) {
            log.info("{}", exception.getClass().getName(), exception);
        }
        assertEquals("edited cohort", editedCohort.getName());
    }


    @Order(9)
    @Test
    void addLoanDetailsToCohort(){
        Cohort editedCohort = new Cohort();
        try{
            Cohort cohort = cohortOutputPort.viewCohortDetails(meedleUserId,program.getId(),cohortTwoId);
            assertNull(cohort.getCohortLoanDetail());
            CohortLoanDetail cohortLoanDetail = getCohortLoanDetail();
            cohort.setCohortLoanDetail(cohortLoanDetail);
            cohort.setLoanBreakdowns(List.of(loanBreakdown));
            log.info("{} = =",cohort);
            editedCohort = cohortOutputPort.saveCohort(cohort);
            log.info("{} = =",editedCohort);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(editedCohort.getCohortLoanDetail());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        log.info("cleanUp : orgainization id {} , userId {} , programId {} , cohortId {}", organizationId, meedleUserId, programId, cohortTwoId);
        identityManagementOutputPort.deleteClient(organizationId);
        identityManagementOutputPort.deleteUser(UserIdentity.builder().id(meedleUserId).build());
//        programOutputPort.deleteProgram(programId);
        cohortOutputPort.deleteCohort(cohortTwoId);
        programCohortOutputPort.delete(programId);
        organizationIdentityOutputPort.delete(organizationId);
        userIdentityOutputPort.deleteUserById(meedleUserId);
        cohortRepository.deleteById(cohortTwoId);
        cohortRepository.deleteById(cohortOneId);
    }
}
