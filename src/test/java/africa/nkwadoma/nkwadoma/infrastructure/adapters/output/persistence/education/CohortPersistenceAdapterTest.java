package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
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
import africa.nkwadoma.nkwadoma.test.data.TestData;
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
import java.util.ArrayList;
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
    private int pageSize ;
    private int pageNumber ;


    @BeforeAll
    void setUpOrg() {
        List<Cohort> cohortSearchResults;
        try {
            cohortSearchResults = cohortOutputPort.findCohortByName("Elite");
            if (cohortSearchResults != null && !cohortSearchResults.isEmpty()) {
                if (ObjectUtils.isNotEmpty(cohortSearchResults.get(0)) && StringUtils.isNotEmpty(cohortSearchResults.get(0).getId())) {
                    cohortOutputPort.deleteCohort(cohortSearchResults.get(0).getId());
                }

            }
        } catch (MeedlException e) {
            log.error("", e);
        }

        meedleUser = TestData.createTestUserIdentity("ade5@gmail.com");
        meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
        organizationIdentity = TestData.createOrganizationTestData("Organization test","RC345687",List.of(employeeIdentity));
        program = TestData.createProgramTestData("My program Ford");
        program2 = TestData.createProgramTestData("My Program Ford 2");
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

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setStartDate(LocalDate.of(2024,10,18));
        xplorers.setProgramId(programId);
        xplorers.setCreatedBy(meedleUserId);
        xplorers.setLoanBreakdowns(loanBreakdowns);
        xplorers.setOrganizationId(organizationId);
        xplorers.setCohortStatus(CohortStatus.CURRENT);

        mavin = new Cohort();
        mavin.setStartDate(LocalDate.of(2024,10,18));
        mavin.setProgramId(programId2);
        mavin.setName("X-wonders");
        mavin.setCreatedBy(meedleUserId);
        mavin.setLoanBreakdowns(loanBreakdowns);
        mavin.setTuitionAmount(BigDecimal.valueOf(20000));
        mavin.setOrganizationId(organizationId);
        mavin.setCohortStatus(CohortStatus.INCOMING);
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
            viewedCohort = cohortOutputPort.findCohort(cohortTwoId);
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

    @Order(6)
    @Test
    void viewAllCohortInAProgram(){

        pageSize = 2;
        pageNumber = 0;
        try{
          Page<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(program.getId(),pageSize,pageNumber);
            assertEquals(2,cohorts.toList().size());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }


    }

    @Order(7)
    @Test
    void searchForCohortInProgram(){
        List<Cohort> cohorts  = new ArrayList<>();
        try{
            cohorts  =
                    cohortOutputPort.searchForCohortInAProgram("X",programId);
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
       assertEquals(2,cohorts.size());
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE, "ndjnhfd,"})
    void deleteCohortWithInvalidId(String cohortId){
        assertThrows(MeedlException.class, ()-> cohortOutputPort.deleteCohort(cohortId));
    }


    @Order(8)
    @Test
    void addLoanDetailsToCohort(){
        Cohort editedCohort = new Cohort();
        try{
            Cohort cohort = cohortOutputPort.findCohort(cohortTwoId);
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
                cohortOutputPort.findAllCohortByOrganizationId(organizationId,pageSize,pageNumber));
    }

    @Test
    void findAllCohortWithNullOrganizationId(){
        assertThrows(MeedlException.class,()->
                cohortOutputPort.findAllCohortByOrganizationId(null,pageSize,pageNumber));
    }

    @Order(9)
    @Test
    void findAllCohortWitOrganizationId() throws MeedlException {
        pageSize = 3;
        pageNumber = 0;
    Page<Cohort> cohorts = cohortOutputPort.findAllCohortByOrganizationId(organizationId,pageSize,pageNumber);
    assertEquals(3,cohorts.getSize());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void searchForCohortWithEmptyName(String emptyName){
        assertThrows(MeedlException.class,()-> cohortOutputPort.findCohortByName(emptyName));
    }

    @Order(10)
    @Test
    void searchForCohortInOrganization(){
        List<Cohort> cohorts  = new ArrayList<>();
        try{
            cohorts = cohortOutputPort.searchCohortInOrganization(organizationId,"x");
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(3,cohorts.size());
    }

    @Order(11)
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
