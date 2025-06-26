package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortLoanDetailPersistenceAdapterTest {

    @Autowired
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private UserIdentity firstUserIdentity;
    private UserIdentity secondUserIdentity;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private List<LoanBreakdown> loanBreakdowns;
    private Program program;
    private Cohort cohort;
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
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private CohortLoanDetail cohortLoanDetail;
    private String mockCohortId = "b59d67ca-293d-4dcf-8a86-067a3334085b";

    @BeforeEach
    public void setUp(){
        try {
            firstUserIdentity = TestData.createTestUserIdentity("ade45@gmail.com");
            firstUserIdentity.setRole(IdentityRole.ORGANIZATION_ADMIN);
            firstUserIdentity = userIdentityOutputPort.save(firstUserIdentity);
            employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(firstUserIdentity);
            employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            organizationIdentity = TestData.createOrganizationTestData("Organization test1","RC3456891", List.of(employeeIdentity));
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            secondUserIdentity = TestData.createTestUserIdentity("loanee@grr.la");
            secondUserIdentity.setRole(IdentityRole.LOANEE);
            secondUserIdentity = userIdentityOutputPort.save(secondUserIdentity);
            program = TestData.createProgramTestData("Software engineer");
            program.setCreatedBy(firstUserIdentity.getId());
            organizationIdentity.setServiceOfferings(List.of(ServiceOffering.builder().name(TRAINING.name()).build()));
            program.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            cohort = TestData.createCohortData("Klaus",program.getId(),
                    organizationIdentity.getId(),loanBreakdowns, secondUserIdentity.getId());
            cohort = cohortOutputPort.save(cohort);
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
            cohortLoanDetail = CohortLoanDetail.builder()
                    .cohort(cohort)
                    .build();
            savedCohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
            log.info("------------> savedCohortLoanDetail ---> {}", savedCohortLoanDetail);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
        assertThat(savedCohortLoanDetail.getId()).isNotNull();
        assertEquals(savedCohortLoanDetail.getCohort().getId(), cohort.getId());
    }

}
