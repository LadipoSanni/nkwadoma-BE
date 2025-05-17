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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanBookAdapterTest {
    @Autowired
    private LoanBookUseCase loanBookUseCase;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    private final String absoluteCSVFilePath = "/Users/admin/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/infrastructure/adapters/output/loanBook/";
    private final String CSVName = "loanBook.csv";
    private LoanBook loanBook;
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
        populateCsvTestFile();
        String loanBookName = "Loan Book Meedl";
        loanBook = TestData.buildLoanBook(absoluteCSVFilePath+CSVName,  loanBookName );
        saveLoanBookCohort();
        saveLoanProduct();
    }

    private void saveLoanProduct() throws MeedlException {
        LoanProduct loanProduct = TestData.buildTestLoanProduct();
        loanProduct = loanProductOutputPort.save(loanProduct);
        loanBook.setLoanProductId(loanProduct.getId());
    }

    private void saveLoanBookCohort() {
        meedleUser = TestData.createTestUserIdentity(TestUtils.generateEmail(4));
        meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
        organizationIdentity = TestData.createOrganizationTestData(TestUtils.generateName(6),"RC3456891",List.of(employeeIdentity));
        program = TestData.createProgramTestData(TestUtils.generateName(6));


        try {
            meedleUser = identityManagerOutputPort.createUser(meedleUser);

            userIdentityOutputPort.save(meedleUser);
//            organizationIdentity.getOrganizationEmployees().forEach(employeeIdentityOutputPort::save);
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            log.info("Organization identity saved before program {}",organizationIdentity);
            organizationId = organizationIdentity.getId();
            meedleUserId = meedleUser.getId();
            program.setCreatedBy(meedleUserId);
            program.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            log.info("Program saved {}",program);
            programId = program.getId();

            LoanBreakdown loanBreakdown = TestData.createLoanBreakDown();
            List<LoanBreakdown> loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));

            Cohort cohort = TestData.createCohortData(TestUtils.generateName(5), program.getId(),
                    program.getOrganizationId(), List.of(TestData.createLoanBreakDown()), meedleUserId);
            cohort = cohortOutputPort.save(cohort);
            loanBook.setCohort(cohort);

        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
    }

    private void populateCsvTestFile() throws IOException {
        Path filePath = Path.of(absoluteCSVFilePath+CSVName);
        Files.write(filePath, List.of(
                "firstName,lastName,email,phoneNumber,DON,initialDeposit,amountRequested,amountReceived",
                "John,Doe,"+TestUtils.generateEmail(5)+",08012345678,2024-01-12,10000,50000,45000",
                "Jane,Smith,"+TestUtils.generateEmail(5)+",08098765432,2024-02-10,15000,60000,60000",
                "David,Johnson,"+TestUtils.generateEmail(5)+",08123456789,2024-03-05,12000,40000,35000",
                "Mary,Brown,"+TestUtils.generateEmail(5)+",08087654321,2024-04-18,8000,30000,30000",
                "Chris,Williams,"+TestUtils.generateEmail(5)+",08111222333,2024-05-09,20000,75000,70000",
                "Linda,Jones,"+TestUtils.generateEmail(5)+",08066778899,2024-06-15,10000,50000,50000",
                "Paul,Miller,"+TestUtils.generateEmail(5)+",08133445566,2024-07-01,9000,35000,30000",
                "Angela,Davis,"+TestUtils.generateEmail(5)+",08099887766,2024-07-20,11000,45000,40000",
                "Mark,Wilson,"+TestUtils.generateEmail(5)+",08177665544,2024-08-11,13000,55000,53000",
                "Grace,Taylor,"+TestUtils.generateEmail(5)+",08055443322,2024-09-03,14000,60000,58000"
        ));
    }

    @Test
    void upLoadExcelSheet() throws MeedlException {
        loanBookUseCase.upLoadFile(loanBook);
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
