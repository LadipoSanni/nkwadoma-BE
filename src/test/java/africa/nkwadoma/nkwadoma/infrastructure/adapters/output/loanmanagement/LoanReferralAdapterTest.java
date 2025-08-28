package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.Page;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class LoanReferralAdapterTest {

    @Autowired
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private CohortLoanee cohortLoanee;
    private UserIdentity userIdentity;
    private UserIdentity meedleUser;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private LoanBreakdown loanBreakdown;
    private List<LoanBreakdown> loanBreakdowns;
    private Program program;
    private Cohort cohort;
    private LoaneeLoanDetail loaneeLoanDetail;
    private Loanee loanee;
    private LoanReferral loanReferral;
    private String loanReferralId;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    private int pageSize = 10;
    private int pageNumber = 0;


    @BeforeAll
    void setUpLoanReferral() {
        try {
            meedleUser = TestData.createTestUserIdentity("ade45@gmail.com");
            meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
            meedleUser = userIdentityOutputPort.save(meedleUser);
            employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
            employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            organizationIdentity = TestData.createOrganizationTestData("Organization test1","RC3456891", List.of(employeeIdentity));
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            userIdentity = TestData.createTestUserIdentity("loanee@grr.la");
            userIdentity.setRole(IdentityRole.LOANEE);
            userIdentity = userIdentityOutputPort.save(userIdentity);
            program = TestData.createProgramTestData("Software engineer");
            program.setCreatedBy(meedleUser.getId());
            organizationIdentity.setServiceOfferings(List.of(ServiceOffering.builder().name(TRAINING.name()).build()));
            program.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns =  loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));
            cohort = TestData.createCohortData("Lacoste",program.getId(),
                    organizationIdentity.getId(),loanBreakdowns,meedleUser.getId());
            cohort = cohortOutputPort.save(cohort);
            loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loanee = TestData.createTestLoanee(userIdentity,loaneeLoanDetail);
            loanee = loaneeOutputPort.save(loanee);
            cohortLoanee = TestData.buildCohortLoanee(loanee, cohort,loaneeLoanDetail,meedleUser.getId());
            cohortLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
            log.info("Cohort Loanee == : {}", cohortLoanee);
            loanReferral = TestData.buildLoanReferral(cohortLoanee,LoanReferralStatus.PENDING);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }


    @Test
    void saveNullLoanReferral() {
        assertThrows(MeedlException.class, () -> loanReferralOutputPort.save(null));
    }

    @Test
    void saveLoanReferralWithNullCohortLoanee() {
        loanReferral.setCohortLoanee(null);
        assertThrows(MeedlException.class, () -> loanReferralOutputPort.save(loanReferral));
    }

    @Test
    void saveLoanReferralWithNullLoaneeInCohortLoanee() {
        loanReferral.getCohortLoanee().setLoanee(null);
        assertThrows(MeedlException.class, () -> loanReferralOutputPort.save(loanReferral));
    }

    @Test
    void saveLoanReferralWithNullLoanReferralStatus() {
        loanReferral.setLoanReferralStatus(null);
        assertThrows(MeedlException.class, () -> loanReferralOutputPort.save(loanReferral));
    }

    @Order(1)
    @Test
    void saveLoanReferal(){

        LoanReferral savedLoanReferral = new LoanReferral();
        try{
            savedLoanReferral = loanReferralOutputPort.save(loanReferral);
            loanReferralId = savedLoanReferral.getId();
        }catch (MeedlException exception){
            log.info("Failed to set up loanReferral {}", exception.getMessage());
        }
        assertEquals(savedLoanReferral.getCohortLoanee().getCohort().getId(),cohortLoanee.getCohort().getId());
        assertEquals(savedLoanReferral.getLoanReferralStatus(),loanReferral.getLoanReferralStatus());
    }

    @Order(2)
    @Test
    void findAllLoanReferrals(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.findAllLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }

    @Order(3)
    @Test
    void findAllLoanReferralsByProgramId(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().programId(program.getId()).pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.findAllLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }


    @Order(4)
    @Test
    void findAllLoanReferralsOrganizationId(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().organizationId(organizationIdentity.getId()).pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.findAllLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }

    @Order(5)
    @Test
    void searchLoanReferrals(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().name("j").pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.searchLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }

     @Order(6)
    @Test
    void searchLoanReferralsWithProgramId(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().name("j").programId(program.getId()).pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.searchLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }

    @Order(7)
    @Test
    void searchLoanReferralsWithOrganizationId(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().name("j").organizationId(organizationIdentity.getId()).pageNumber(pageNumber).pageSize(pageSize).build();
        try{
            foundLoanReferral = loanReferralOutputPort.searchLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(1, foundLoanReferral.getTotalElements());
    }

    @Order(8)
    @Test
    void searchLoanReferralsWithWithNotExistingLoaneeName(){
        Page<LoanReferral> foundLoanReferral = Page.empty();
        LoanReferral request = LoanReferral.builder().name("z").pageNumber(pageNumber).pageSize(pageSize).build();
        try {
            foundLoanReferral = loanReferralOutputPort.searchLoanReferrals(request);
        }catch (MeedlException meedlException){
            log.info("Failed to find loanReferrals {}", meedlException.getMessage());
        }
        assertEquals(0, foundLoanReferral.getTotalElements());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        log.info("loan referal id = {}", loanReferralId);
        loanReferralOutputPort.deleteLoanReferral(loanReferralId);
        log.info("cohort loanee id = {}", cohortLoanee.getId());
        cohortLoaneeOutputPort.delete(cohortLoanee.getId());
        log.info("cohort id = {}", cohort.getId());
        cohortOutputPort.deleteCohort(cohort.getId());
        log.info("loanee id = {}", loanee.getId());
        loaneeOutputPort.deleteLoanee(loanee.getId());
        log.info("loanee loane details id = {}", loaneeLoanDetail.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
        log.info("loan breakdowns = {}", loanBreakdowns);
        loanBreakdownOutputPort.deleteAll(loanBreakdowns);
        log.info("program id = {}", program.getId());
        programOutputPort.deleteProgram(program.getId());
        log.info("org id = {}", organizationIdentity.getId());
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        log.info("org empoyee  = {}", employeeIdentity.getId());
        organizationEmployeeIdentityOutputPort.delete(employeeIdentity.getId());
        log.info("meedl id = {}", meedleUser.getId());
        userIdentityOutputPort.deleteUserById(meedleUser.getId());
        log.info("user id = {}", userIdentity.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
    }

}
 