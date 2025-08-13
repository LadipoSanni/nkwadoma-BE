package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramRepository;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CohortPersistenceAdapterTest {
    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;
    private Cohort xplorers;
    private Cohort mavin;
    @Autowired
    private CohortRepository cohortRepository;
    private String meedleUserId;
    private UserIdentity meedleUser;
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
    private ProgramCohortOutputPort programCohortOutputPort;
    @Autowired
    private LoanDetailsOutputPort loanDetailsOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    private Program program;
    private Program program2;
    private String programId;
    private String programId2;
    private String cohortOneId;
    private String cohortTwoId;
    private String cohortThreeId;
    private String organizationId;
    private LoanBreakdown loanBreakdown;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private LoanDetail loanDetail;
    private List<LoanBreakdown> loanBreakdowns;
    private int pageSize = 10 ;
    private int pageNumber = 0;
    @Autowired
    private ProgramRepository programRepository;


    @BeforeAll
    void setUpOrg() {
        meedleUser = TestData.createTestUserIdentity("ade45@gmail.com");
        meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
        organizationIdentity = TestData.createOrganizationTestData("Organization test1","RC3456891",List.of(employeeIdentity));
        program = TestData.createProgramTestData(TestUtils.generateName(5));
        program2 = TestData.createProgramTestData(TestUtils.generateName(5));
        loanDetail = TestData.createLoanDetail();
        loanBreakdown = TestData.createLoanBreakDown();

        try {
            Optional<UserIdentity> userByEmail = identityManagementOutputPort.getUserByEmail(meedleUser.getEmail());
            if (userByEmail.isPresent()) {
                identityManagementOutputPort.deleteUser(userByEmail.get());
            }
            meedleUser = identityManagementOutputPort.createUser(meedleUser);
            userIdentityOutputPort.save(meedleUser);
            organizationIdentity.getOrganizationEmployees().forEach(employeeIdentityOutputPort::save);
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            log.info("Organization identity saved before program {}",organizationIdentity);
            organizationId = organizationIdentity.getId();
            meedleUserId = meedleUser.getId();
            program.setCreatedBy(meedleUserId);
            program2.setCreatedBy(meedleUserId);
            program.setOrganizationIdentity(organizationIdentity);
            program2.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            program2 = programOutputPort.saveProgram(program2);
            log.info("Program saved {}",program);
            programId = program.getId();
            programId2 = program2.getId();
            loanDetail = loanDetailsOutputPort.saveLoanDetails(loanDetail);
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));
        } catch (MeedlException e) {
            log.info("Failed to save program {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setUp(){
        log.info("progam id is --- {}", program.getId());
        elites = new Cohort();
        elites.setStartDate(LocalDate.of(2024,10,18));
        elites.setProgramId(program.getId());
        elites.setName("X-men");
        elites.setCreatedBy(meedleUserId);
        elites.setLoanBreakdowns(loanBreakdowns);
        elites.setTuitionAmount(BigDecimal.valueOf(20000));
        elites.setOrganizationId(organizationId);
        elites.setCohortStatus(CohortStatus.GRADUATED);
        elites.setCohortType(CohortType.NON_LOAN_BOOK);

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setStartDate(LocalDate.of(2024,10,18));
        xplorers.setProgramId(programId);
        xplorers.setCreatedBy(meedleUserId);
        xplorers.setLoanBreakdowns(loanBreakdowns);
        xplorers.setTuitionAmount(BigDecimal.valueOf(20000));
        xplorers.setOrganizationId(organizationId);
        xplorers.setCohortStatus(CohortStatus.CURRENT);
        xplorers.setCohortType(CohortType.NON_LOAN_BOOK);

        mavin = new Cohort();
        mavin.setStartDate(LocalDate.of(2024,10,18));
        mavin.setProgramId(programId2);
        mavin.setName("X-wonders");
        mavin.setCreatedBy(meedleUserId);
        mavin.setLoanBreakdowns(loanBreakdowns);
        mavin.setTuitionAmount(BigDecimal.valueOf(20000));
        mavin.setOrganizationId(organizationId);
        mavin.setCohortStatus(CohortStatus.INCOMING);
        mavin.setCohortType(CohortType.NON_LOAN_BOOK);
    }


    @Test
    void saveCohortWithNullCohort(){
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(null));
    }
    @Test
    void saveCohortWithNullProgramId(){
        elites.setProgramId(null);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithValidProgramId(String programId){
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithEmptyName(String name){
        elites.setName(name);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " ", "email@gmail.com","3gdgttebdindndd673ydieyendjdljdh"})
    void saveCohortWithInvalidCreator(String createdBy){
        elites.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }
    @Test
    void saveCohortWithNullStartDate(){
        elites.setStartDate(null);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }

    @Test
    void cannotSaveCohortWithNegativeTuitionAmount(){
        elites.setTuitionAmount(BigDecimal.valueOf(-1));
        assertThrows(MeedlException.class, ()-> cohortOutputPort.save(elites));
    }

    @Order(1)
    @Test
    void saveCohort() {
        try {
            Cohort cohort = cohortOutputPort.save(elites);
            assertNotNull(cohort);
            assertNotNull(cohort.getId());
            cohortOneId = cohort.getId();
            assertEquals(cohort.getName(), elites.getName());
            log.info("cohort {}",cohort);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(2)
    @Test
    void saveAnotherCohortInProgram() {
        try {
            Cohort cohort = cohortOutputPort.save(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            cohortTwoId = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(3)
    @Test
    void saveCohortInAnotherProgram() {
        try {
            Cohort cohort = cohortOutputPort.save(mavin);
            assertEquals(cohort.getName(), mavin.getName());
            cohortThreeId = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Order(4)
    @Test
    void viewCohortDetails(){
        Cohort viewedCohort = new Cohort() ;
        try{
            viewedCohort = cohortOutputPort.findCohortById(cohortTwoId);
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(xplorers.getName(), viewedCohort.getName());
        assertEquals(xplorers.getCreatedBy(), viewedCohort.getCreatedBy());
    }


    @Test
    void viewCohortWithNullCohortId(){
        assertThrows(MeedlException.class, () -> cohortOutputPort.findCohortById(
                null));
    }


    @Order(5)
    @Test
    void viewAllCohortInAProgram(){

        pageSize = 2;
        pageNumber = 0;
        elites.setPageSize(pageSize);
        elites.setPageNumber(pageNumber);
        try{
          Page<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(elites);
            assertEquals(1,cohorts.toList().size());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }


    }

    @Order(6)
    @Test
    void searchForCohortInProgram(){
        Page<Cohort> cohorts  = Page.empty();
        try{
            cohorts  =
                    cohortOutputPort.searchForCohortInAProgram("X",programId,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
       assertEquals(2,cohorts.getContent().size());
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE, "ndjnhfd,"})
    void deleteCohortWithInvalidId(String cohortId){
        assertThrows(MeedlException.class, ()-> cohortOutputPort.deleteCohort(cohortId));
    }


    @Order(7)
    @Test
    void addLoanDetailsToCohort(){
        Cohort editedCohort = new Cohort();
        try{
            Cohort cohort = cohortOutputPort.findCohortById(cohortTwoId);
            assertNull(cohort.getLoanDetail());
            cohort.setLoanDetail(loanDetail);
            log.info("{} = =",cohort);
            editedCohort = cohortOutputPort.save(cohort);
            log.info("{} = =",editedCohort);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(editedCohort.getLoanDetail());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE,StringUtils.EMPTY,"hhjhjjsdhhhdshhjdhsh"})
    void findAllCohortWithInvalidOrganizationId(String organizationId){
        assertThrows(MeedlException.class,()->
                cohortOutputPort.findAllCohortByOrganizationId(organizationId,elites));
    }

    @Test
    void findAllCohortWithNullOrganizationId(){
        assertThrows(MeedlException.class,()->
                cohortOutputPort.findAllCohortByOrganizationId(null,elites));
    }

    @Order(8)
    @Test
    void findAllCohortWitOrganizationId() throws MeedlException {
        pageSize = 3;
        pageNumber = 0;
        elites.setPageSize(pageSize);
        elites.setPageNumber(pageNumber);
    Page<Cohort> cohorts = cohortOutputPort.findAllCohortByOrganizationId(organizationId,elites);
    assertEquals(3,cohorts.getSize());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE})
    void searchForCohortWithEmptyName(String emptyName){
        elites.setName(emptyName);
        elites.setPageSize(pageSize);
        elites.setPageNumber(pageNumber);
         Page<Cohort> cohorts = Page.empty();
         try{
          cohorts = cohortOutputPort.findCohortByNameAndOrganizationId(elites);
         }catch (MeedlException exception){
             log.info("{} {}", exception.getClass().getName(), exception.getMessage());
         }
          assertEquals(0,cohorts.getContent().size());
    }

    @Order(9)
    @Test
    void searchForCohortInOrganization(){
        Page<Cohort> cohorts  = Page.empty();
        try{
            cohorts = cohortOutputPort.searchCohortInOrganization(organizationId,"x",pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(3,cohorts.getContent().size());
    }

    @Order(10)
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

    @AfterAll
    void cleanUp() throws MeedlException {
        log.info("cleanUp : orgainization id {} , userId {} , programId {} , cohortId {}", organizationId, meedleUserId, programId, cohortTwoId);
        identityManagementOutputPort.deleteUser(meedleUser);
        cohortOutputPort.deleteCohort(cohortTwoId);
        cohortOutputPort.deleteCohort(cohortThreeId);
        programCohortOutputPort.delete(programId);
        programCohortOutputPort.delete(programId2);
        organizationIdentityOutputPort.delete(organizationId);
        userIdentityOutputPort.deleteUserById(meedleUserId);
    }
}
