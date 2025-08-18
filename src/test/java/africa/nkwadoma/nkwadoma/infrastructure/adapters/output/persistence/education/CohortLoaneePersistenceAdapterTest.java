package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortLoaneePersistenceAdapterTest {

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
    private String cohortLoaneeId;
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


    @BeforeAll
    void setUpCohortLoanee() {
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
            log.info("Cohort Loanee == : {}", cohortLoanee);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }


    @Test
    void saveNullCohortLoanee() {
        assertThrows(MeedlException.class, () -> cohortLoaneeOutputPort.save(null));
    }

    @Test
    void saveCohortLoaneeWithNullCohort() {
        cohortLoanee.setCohort(null);
        assertThrows(MeedlException.class, () -> cohortLoaneeOutputPort.save(null));
    }

    @Test
    void saveCohortLoaneeWithNullLoanee() {
        cohortLoanee.setLoanee(null);
        assertThrows(MeedlException.class, () -> cohortLoaneeOutputPort.save(null));
    }

    @Test
    void saveCohortLoaneeWithNullCreatedBy(){
        cohortLoanee.setCreatedBy(null);
        assertThrows(MeedlException.class, () -> cohortLoaneeOutputPort.save(null));
    }

    @Order(1)
    @Test
    void saveCohortLoanee(){
        CohortLoanee savedLoanee;
        try{
            log.info("Saving cohort loanee {}", cohortLoanee);
            savedLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
            cohortLoaneeId = savedLoanee.getId();
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertEquals(savedLoanee.getLoanee().getId(),loanee.getId());
        assertEquals(savedLoanee.getCohort().getId(),cohort.getId());
        assertEquals(savedLoanee.getCreatedBy(),meedleUser.getId());
    }

    @Test
    void findCohortLoaneeByLoaneeIdAndCohortIdByNullLoaneeId(){
        assertThrows(MeedlException.class, () ->
                cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(null,cohort.getId()));
    }

    @Test
    void findCohortLoaneeByLoaneeIdAndCohortIdByNullCohortId(){
        assertThrows(MeedlException.class, () ->
                cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loanee.getId(),null));
    }


    @Order(2)
    @Test
    void findCohortLoaneeByLoaneeIdAndCohortId(){
        CohortLoanee foundCohortLoanee;
        try {
            foundCohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loanee.getId(),cohort.getId());
        } catch (MeedlException exception) {
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertEquals(foundCohortLoanee.getLoanee().getUserIdentity().getFirstName(),loanee.getUserIdentity().getFirstName());
    }


    @Test
    void findCohortLoaneeByLoaneeIdAndProgramIdByNullLoaneeId(){
        assertThrows(MeedlException.class, () ->
                cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(cohort.getProgramId(),null));
    }

    @Test
    void findCohortLoaneeByLoaneeIdAndProgramIdByNullProgramId(){
        assertThrows(MeedlException.class, () ->
                cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(null,loanee.getId()));
    }

    @Order(3)
    @Test
    void findCohortLoaneeByProgramIdAndLoaneeId(){
        CohortLoanee foundCohortLoanee;
        try {
            foundCohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByProgramIdAndLoaneeId(cohort.getProgramId(),loanee.getId());
        } catch (MeedlException exception) {
            log.info("Failed to find  cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertEquals(foundCohortLoanee.getLoanee().getId(),loanee.getId());
        assertEquals(foundCohortLoanee.getCohort().getId(),cohort.getId());
        assertEquals(foundCohortLoanee.getCreatedBy(),meedleUser.getId());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        log.info("cohort loanee id = {}", cohortLoaneeId);
        cohortLoaneeOutputPort.delete(cohortLoaneeId);
        log.info("cohort id = {}", cohortLoaneeId);
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
