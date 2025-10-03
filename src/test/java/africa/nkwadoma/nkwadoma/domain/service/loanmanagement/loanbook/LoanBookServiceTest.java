package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.InstituteMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.CohortType;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanBookServiceTest {

    @Autowired
    private LoanBookUseCase loanBookUseCase;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    private final String absoluteCSVFilePath = "/Users/admin/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/domain/service/loanManagement/loanBook/";
//    private final String absoluteCSVFilePath = "/Users/qudusadeshina/IdeaProjects/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/domain/service/loanManagement/loanBook/";
    //    /Users/qudusadeshina/IdeaProjects/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/domain/service/loanManagement/loanBook
    private final String loanBookCSVName = "loanBook.csv";
    private final String repaymentRecordBookCSVName = "repaymentRecordBook.csv";
    private LoanBook loanBook;
    private LoanBook repaymentRecordBook;
    private Cohort cohort ;
    private UserIdentity meedleUser;
    private Program program;
    private String organizationId;
    private String meedleUserId;
    private String programId;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;
    @Autowired
    private LoanProductVendorRepository loanProductVendorRepository;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private LoanProduct loanProduct;
    @Autowired
    private VendorEntityRepository vendorEntityRepository;
    @Autowired
    private InstituteMetricsOutputPort instituteMetricsOutputPort;

    @BeforeAll
    void setUp() throws IOException, MeedlException {
//        populateCsvTestFile();
        loanBook = TestData.buildLoanBook(absoluteCSVFilePath+ loanBookCSVName);
        repaymentRecordBook = TestData.buildLoanBook(absoluteCSVFilePath+ repaymentRecordBookCSVName);

        cohort = saveLoanBookCohort();
        LoanProduct loanProduct = saveLoanProduct();

        loanBook.setCohort(cohort);
        repaymentRecordBook.setCohort(cohort);
        loanBook.setLoanProductId(loanProduct.getId());
        repaymentRecordBook.setLoanProductId(loanProduct.getId());
        log.info("Loan book cohort id {}", loanBook.getCohort().getId());

    }

    private LoanProduct saveLoanProduct() throws MeedlException {
         loanProduct = TestData.buildTestLoanProduct();
        loanProduct = loanProductOutputPort.save(loanProduct);
        return loanProduct;
    }

    private Cohort saveLoanBookCohort() {
        meedleUser = TestData.createTestUserIdentity(TestUtils.generateEmail(4));
        meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
        organizationIdentity = TestData.createOrganizationTestData(TestUtils.generateName(6), "RC3456891", List.of(employeeIdentity));
        program = TestData.createProgramTestData(TestUtils.generateName(6));


        Cohort cohort;
        try {
            meedleUser = identityManagerOutputPort.createUser(meedleUser);

            userIdentityOutputPort.save(meedleUser);
//            organizationIdentity.getOrganizationEmployees().forEach(employeeIdentityOutputPort::save);
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            log.info("Organization identity saved before program {}", organizationIdentity);
            organizationId = organizationIdentity.getId();
            meedleUserId = meedleUser.getId();
            program.setCreatedBy(meedleUserId);
            program.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            log.info("Program saved {}", program);
            programId = program.getId();

            LoanBreakdown loanBreakdown = TestData.createLoanBreakDown();
            List<LoanBreakdown> loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));

            cohort = TestData.createCohortData(TestUtils.generateName(5), program.getId(),
                    program.getOrganizationId(), List.of(TestData.createLoanBreakDown()), meedleUserId);
            cohort.setCohortType(CohortType.LOAN_BOOK);
            cohort = cohortOutputPort.save(cohort);

        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        return cohort;
    }

    private void populateCsvTestFile() throws IOException {
        List<String> emails = TestUtils.generateRandomLoanBookCSV(absoluteCSVFilePath+ loanBookCSVName, 10);
        TestUtils.generateRandomRepaymentRecordCSV(emails, absoluteCSVFilePath+ repaymentRecordBookCSVName);
    }

    @Test
    void upLoadCsvSheet() throws MeedlException {
        log.info("Cohort before upload in test");
        loanBook.setCohort(cohort);
        log.info("Loan book before upload in test {}", loanBook);
//        loanBookUseCase.upLoadFile(loanBook);
    }


    @Test
    void getAllRepaymentsMatchingSingleEmail() {
        String email = "test@example.com";

        RepaymentHistory match1 = createRepayment(UUID.randomUUID().toString(), email);
        RepaymentHistory match2 = createRepayment(UUID.randomUUID().toString(), email);
        RepaymentHistory nonMatch = createRepayment(UUID.randomUUID().toString(), "other@example.com");

        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(match1, match2, nonMatch), email
        );

        assertEquals(2, result.size());
        assertTrue(result.contains(match1));
        assertTrue(result.contains(match2));
    }

    @Test
    void getEmptyListWhenEmailDoesntMatch() {
        String email = "test@example.com";
        RepaymentHistory nonMatch1 = createRepayment(UUID.randomUUID().toString(), "nope1@example.com");
        RepaymentHistory nonMatch2 = createRepayment(UUID.randomUUID().toString(), "nope2@example.com");

        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(nonMatch1, nonMatch2), email
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSkipRepaymentWhenLoaneeIsNull() {
        RepaymentHistory rh = RepaymentHistory.builder()
                .id(UUID.randomUUID().toString())
                .loanee(null)
                .build();

        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(rh), "any@example.com"
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSkipRepaymentWhenUserIdentityIsNull() {
        Loanee loanee = Loanee.builder().userIdentity(null).build();
        RepaymentHistory rh = RepaymentHistory.builder()
                .id(UUID.randomUUID().toString())
                .loanee(loanee)
                .build();

        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(rh), "any@example.com"
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSkipRepaymentWhenUserIdentityEmailIsNull() {
        UserIdentity userIdentity = UserIdentity.builder().email(null).build();
        Loanee loanee = Loanee.builder().userIdentity(userIdentity).build();
        RepaymentHistory rh = RepaymentHistory.builder()
                .id(UUID.randomUUID().toString())
                .loanee(loanee)
                .build();

        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(rh), "any@example.com"
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyListWhenRepaymentHistoriesIsEmpty() {
        List<RepaymentHistory> result = loanBookUseCase.getRepaymentsByEmail(
                List.of(), "test@example.com"
        );

        assertTrue(result.isEmpty());
    }

    private RepaymentHistory createRepayment(String id, String email) {
        UserIdentity userIdentity = UserIdentity.builder()
                .id(id)
                .email(email)
                .build();

        Loanee loanee = Loanee.builder()
                .id(id)
                .userIdentity(userIdentity)
                .build();

        return RepaymentHistory.builder()
                .id(id)
                .loanee(loanee)
                .paymentDateTime(LocalDateTime.now())
                .month(7)
                .build();
    }

    @AfterAll
    void tearDown() throws MeedlException {
        VendorEntity foundGemsVendorEntity = vendorEntityRepository.findByVendorName(loanProduct.getVendors().get(0).getVendorName());

        loanProductVendorRepository.deleteByVendorEntityId((foundGemsVendorEntity.getId()));

        LoanProduct foundGoldLoanProduct = loanProductOutputPort.findByName(loanProduct.getName());
        loanProductOutputPort.deleteById(foundGoldLoanProduct.getId());

        log.info("cohort id = {}", cohort.getId());
        cohortOutputPort.deleteCohort(cohort.getId());

        log.info("program id = {}", program.getId());
        programOutputPort.deleteProgram(program.getId());
        log.info("org id = {}", organizationIdentity.getId());
        InstituteMetrics instituteMetrics = instituteMetricsOutputPort.findByOrganizationId(organizationId);
        if (ObjectUtils.isNotEmpty(instituteMetrics)){
            log.info("Metrics was found for this organization");
            instituteMetricsOutputPort.delete(instituteMetrics.getId());
        }
        instituteMetricsOutputPort.deleteByOrganizationId(organizationId);
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        log.info("meedl id = {}", meedleUser.getId());
        userIdentityOutputPort.deleteUserById(meedleUser.getId());
        identityManagerOutputPort.deleteUser(meedleUser);
    }

}
