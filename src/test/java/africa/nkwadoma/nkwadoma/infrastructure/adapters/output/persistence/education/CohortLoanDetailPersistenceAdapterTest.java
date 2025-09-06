package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortLoanDetailPersistenceAdapterTest {

    @Autowired
    private InstituteMetricsOutputPort instituteMetricsOutputPort;
    private UserIdentity userIdentity;
    private UserIdentity meedleUser;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private LoanBreakdown loanBreakdown;
    private List<LoanBreakdown> loanBreakdowns;
    private Program program;
    private Cohort cohort;
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
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private CohortLoanDetail cohortLoanDetail;
    private String cohortLoanDetailId;
    private String cohortId;

    @BeforeEach
    public void setUp(){
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
            cohortLoanDetail = TestData.buildCohortLoanDetail(cohort);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }


    @Test
    void saveNullCohortLoanDetail() {
        assertThrows(MeedlException.class, () -> cohortLoanDetailOutputPort.save(null));
    }

    @Order(1)
    @Test
    void saveCohortLoanDetail(){
        CohortLoanDetail savedCohortLoanDetail = null;
        try{
            log.info("Input object -----> {}", cohortLoanDetail);
            savedCohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
            cohortLoanDetailId = savedCohortLoanDetail.getId();
            cohortId = cohortLoanDetail.getCohort().getId();
            log.info("------------> savedCohortLoanDetail ---> {}", savedCohortLoanDetail);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertThat(savedCohortLoanDetail.getId()).isNotNull();
        assertEquals(savedCohortLoanDetail.getCohort().getId(), cohort.getId());
    }

    @Test
    void findCohortLoanDetailByNullCohortId(){
        assertThrows(MeedlException.class, () -> cohortLoanDetailOutputPort.findByCohortId(null));
    }

    @Order(2)
    @Test
    void findByCohortId(){
        CohortLoanDetail savedCohortLoanDetail = null;
        try{
            log.info("Input object -----> {}", cohort.getId());
            log.info("Input object 2 -----> {}", cohortId);
            savedCohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(cohortId);
            log.info("found cohort loan detail ---> {}", savedCohortLoanDetail);
            cohortLoanDetailId = savedCohortLoanDetail.getId();
            log.info("------------> found  ---> {}", savedCohortLoanDetail);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertThat(savedCohortLoanDetail.getId()).isNotNull();
        assertEquals(savedCohortLoanDetail.getCohort().getId(), cohortId);
    }

    @AfterAll
    void tearDown() throws MeedlException {

        log.info("cohort loan detail id is {}", cohortLoanDetailId);
        cohortLoanDetailOutputPort.delete(cohortLoanDetailId);
        log.info("cohort id = {}", cohort.getId());
        cohortOutputPort.deleteCohort(cohort.getId());
        log.info("loan breakdowns = {}", loanBreakdowns);
        loanBreakdownOutputPort.deleteAll(loanBreakdowns);
        log.info("program id = {}", program.getId());
        programOutputPort.deleteProgram(program.getId());
        log.info("org id = {}", organizationIdentity.getId());
        InstituteMetrics instituteMetrics = instituteMetricsOutputPort.findByOrganizationId(organizationIdentity.getId());
        if (ObjectUtils.isNotEmpty(instituteMetrics)){
            log.info("Metrics was found for this organization");
            instituteMetricsOutputPort.delete(instituteMetrics.getId());
        }
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        log.info("org empoyee  = {}", employeeIdentity.getId());
        organizationEmployeeIdentityOutputPort.delete(employeeIdentity.getId());
        log.info("meedl id = {}", meedleUser.getId());
        userIdentityOutputPort.deleteUserById(meedleUser.getId());
        log.info("user id = {}", userIdentity.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
    }
}
