package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanBookServiceTest {
    @Autowired
    private LoanBookUseCase loanBookUseCase;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    private final String absoluteCSVFilePath = "/Users/admin/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/domain/service/loanManagement/loanBook/";
    private final String CSVName = "loanBook.csv";
    private LoanBook loanBook;
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

    @BeforeAll
    void setUp() throws IOException, MeedlException {
//        populateCsvTestFile();
        loanBook = TestData.buildLoanBook(absoluteCSVFilePath+CSVName );

        cohort = saveLoanBookCohort();
        LoanProduct loanProduct = saveLoanProduct();

        loanBook.setCohort(cohort);
        loanBook.setLoanProductId(loanProduct.getId());
        log.info("Loan book cohort id {}", loanBook.getCohort().getId());



    }

    private LoanProduct saveLoanProduct() throws MeedlException {
        LoanProduct loanProduct = TestData.buildTestLoanProduct();
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
            cohort = cohortOutputPort.save(cohort);

        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        return cohort;
    }

    private void populateCsvTestFile() throws IOException {
        TestUtils.generateRandomCSV(absoluteCSVFilePath+CSVName, 10);
    }

    @Test
    void upLoadCsvSheet() throws MeedlException {
        log.info("Cohort before upload in test");
        loanBook.setCohort(cohort);
        log.info("Loan book before upload in test {}", loanBook);
//        loanBookUseCase.upLoadFile(loanBook);
    }
    @Test
    void uploadLoanBookWithNull(){
        assertThrows(MeedlException.class,() -> loanBookUseCase.upLoadFile(null));
    }
    @Test
    void uploadLoanBookWithNullCohort(){
        loanBook.setCohort(null);
        assertThrows(MeedlException.class,() -> loanBookUseCase.upLoadFile(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "", "288b3cf9-7106-4405-9061-7cd92aceb474"})
    void uploadLoanBookWithInvalidCohortId(String id){
        Cohort cohort = Cohort.builder().id(id).build();
        loanBook.setCohort(cohort);
        assertThrows(MeedlException.class,() -> loanBookUseCase.upLoadFile(loanBook));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "", "288b3cf9-7106-4405-9061-7cd92aceb474"})
    void uploadLoanBookWithInvalidActorId(String id){
        Cohort cohort = Cohort.builder().createdBy(id).build();
        loanBook.setCohort(cohort);
        assertThrows(MeedlException.class,() -> loanBookUseCase.upLoadFile(loanBook));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "", "288b3cf9-7106-4405-9061-7cd92aceb474"})
    void uploadLoanBookWithInvalidLoanProductId(String id){
        loanBook.setLoanProductId(id);
        assertThrows(MeedlException.class,() -> loanBookUseCase.upLoadFile(loanBook));
    }


}
