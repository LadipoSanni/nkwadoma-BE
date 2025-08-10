package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.time.*;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanAdapterTest {



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
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;
    private LoanRequest loanRequest;
    @Autowired
    private LoanOfferOutputPort loanOfferOutputPort;
    private LoanOffer loanOffer;
    private LoanProduct loanProduct;
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;
    @Autowired
    private VendorEntityRepository vendorEntityRepository;
    @Autowired
    private LoanProductVendorRepository loanProductVendorRepository;
    @Autowired
    private LoanOutputPort loanOutputPort;
    private Loan loan;
    private String loanId;
    private final String organizationName = "Organization test1";
    int pageSize = 10;
    int pageNumber = 0;

    @BeforeAll
    void setUpLoanOffer() {
        try {
            meedleUser = TestData.createTestUserIdentity("ade45@gmail.com");
            meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
            meedleUser = userIdentityOutputPort.save(meedleUser);
            employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
            employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            organizationIdentity = TestData.createOrganizationTestData(organizationName,"RC3456891", List.of(employeeIdentity));
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
            cohortLoanee.setReferredBy(organizationIdentity.getName());
            cohortLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
            log.info("Cohort Loanee == : {}", cohortLoanee);
            loanReferral = TestData.buildLoanReferral(cohortLoanee,LoanReferralStatus.PENDING);
            loanReferral = loanReferralOutputPort.save(loanReferral);
            loanReferralId = loanReferral.getId();
            loanRequest = TestData.buildLoanRequest(loanReferral.getId());
            loanRequest = loanRequestOutputPort.save(loanRequest);
            loanOffer = TestData.buildLoanOffer(loanRequest);
            loanProduct = TestData.buildTestLoanProduct();
            loanProduct = loanProductOutputPort.save(loanProduct);
            loanOffer.setLoanProduct(loanProduct);
            loanOffer = loanOfferOutputPort.save(loanOffer);
            loan = TestData.buildLoan(loanOffer.getId());
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }


    @Test
    void saveNullLoan(){
        assertThrows(MeedlException.class, () -> loanOutputPort.save(null));
    }

    @Test
    void saveLoanWithNullLoanOfferId(){
        loan.setLoanOffer(null);
        assertThrows(MeedlException.class, () -> loanOutputPort.save(null));
    }

    @Test
    void saveLoanWithNullLoanAccountId(){
        loan.setLoanAccountId(null);
        assertThrows(MeedlException.class, () -> loanOutputPort.save(null));
    }

    @Test
    void saveLoanWithNullLoanStatus(){
        loan.setLoanStatus(null);
        assertThrows(MeedlException.class, () -> loanOutputPort.save(null));
    }

    @Test
    void saveLoanWithNullStartDate() {
        loan.setStartDate(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }


    @Test
    @Order(1)
    void saveLoan(){
        Loan savedLoan;
        try {
            log.info("loan object {}", loan);
            savedLoan = loanOutputPort.save(loan);
            loanId = savedLoan.getId();
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getId());
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void findLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.findLoanById(id));
    }


    @Test
    @Order(2)
    void findLoanById() {
        Loan loan;
        try {
            log.info("loan id before finding : {}", loanId);
            loan = loanOutputPort.findLoanById(loanId);
            log.info("loan id after finding : {}", loan);
        } catch (MeedlException e) {
            log.error("Error getting loan {}", e.getMessage());
            throw new RuntimeException(e);
        }
        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(loan.getId(), loanId);

    }

    @Test
    @Order(3)
    void findAllLoanByOrganizationId() {
        Page<Loan> loans = Page.empty();
        try {
            log.info("organization id before finding : {}", organizationIdentity.getId());
            loans = loanOutputPort.findAllByOrganizationId(organizationIdentity.getId(), 10, 0);
        } catch (MeedlException e) {
            log.error("Error finding loans by organization: {}", e.getMessage());
        }

        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }


    @Test
    @Order(4)
    void findAllLoan(){

        Page<Loan> loans = Page.empty();
        try{
            loans = loanOutputPort.findAllLoan(pageSize,pageNumber);
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }

    @Test
    @Order(5)
    void findAllLoanDisbursalByOrganizationId(){

        Page<Loan> loans = Page.empty();
        try{
            loans = loanOutputPort.findAllByOrganizationId(organizationIdentity.getId(), pageSize,pageNumber);
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }

    @Test
    @Order(6)
    void findLoanReferralByLoanId() {
        String referBy = "";
        try {
           referBy = loanOutputPort.findLoanReferal(loanId);
        }catch (MeedlException e){
            log.error("Error finding loan referral : {}", e.getMessage());
        }
        assertEquals(organizationName,referBy);

    }

    @Test
    @Order(7)
    void findAllLoanDisbursedToALoaneeByUserId(){

        Page<Loan> loans = Page.empty();
        try{
            loans = loanOutputPort.findAllLoanDisburedToLoanee(userIdentity.getId(), pageSize,pageNumber);
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }

    @Test
    @Order(9)
    void searchAllLoanDisbursalByOrganizationNameAndLoaneeId(){
        Page<Loan> loans = Page.empty();
        try{
            Loan loanQuery = Loan.builder().organizationName("o").loaneeId(loanee.getId()).pageSize(pageSize).pageNumber(pageNumber).build();
            loans = loanOutputPort.searchLoanByOrganizationNameAndLoaneeId(loanQuery);
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }


      @Test
    @Order(10)
    void searchAllLoanDisbursalByOrganizationNameAndUserId(){
        Page<Loan> loans = Page.empty();
        try{
            Loan loanQuery = Loan.builder().organizationName("o").pageSize(pageSize).pageNumber(pageNumber).build();
            loans = loanOutputPort.searchLoanByOrganizationNameAndUserId(loanQuery,userIdentity.getId());
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }

    @Test
    void searchAllLoanDisbursalByNotExistingOrganizationNameAndUserId(){
        Page<Loan> loans = Page.empty();
        try{
            Loan loanQuery = Loan.builder().organizationName("z").pageSize(pageSize).pageNumber(pageNumber).build();
            loans = loanOutputPort.searchLoanByOrganizationNameAndUserId(loanQuery,userIdentity.getId());
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        VendorEntity foundGemsVendorEntity = vendorEntityRepository.findByVendorName(loanProduct.getVendors().get(0).getVendorName());
        loanProductVendorRepository.deleteByVendorEntityId((foundGemsVendorEntity.getId()));
        vendorEntityRepository.deleteById(foundGemsVendorEntity.getId());
        loanOutputPort.deleteById(loanId);

        loanOfferOutputPort.deleteLoanOfferById(loanReferralId);
        loanProductOutputPort.deleteById(loanProduct.getId());

        loanRequestOutputPort.deleteLoanRequestById(loanReferralId);
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