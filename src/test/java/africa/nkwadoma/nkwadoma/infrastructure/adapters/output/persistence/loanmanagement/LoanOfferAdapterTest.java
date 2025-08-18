package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanOfferAdapterTest {
    //TOdo Coming back to write proper test

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
    private int pageSize = 10;
    private int pageNumber = 0;



    @BeforeAll
    void setUpLoanOffer() {
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
            loanReferral = loanReferralOutputPort.save(loanReferral);
            loanReferralId = loanReferral.getId();
            loanRequest = TestData.buildLoanRequest(loanReferral.getId());
            loanRequest = loanRequestOutputPort.save(loanRequest);
            loanOffer = TestData.buildLoanOffer(loanRequest);
            loanProduct = TestData.buildTestLoanProduct();
            loanProduct = loanProductOutputPort.save(loanProduct);
            loanOffer.setLoanProduct(loanProduct);
        }catch (MeedlException exception){
            log.info("Failed to set up cohort loanee {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }




    @Test
    void saveNullLoanOffer() {
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(null));
    }


    @Test
    void saveLoanOfferWithNullId() {
        loanOffer.setId(null);
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(loanOffer));
    }

    @Test
    void saveLoanOfferWithNullLoanProduct() {
        loanOffer.setLoanProduct(null);
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(loanOffer));
    }

    @Test
    void saveLoanOfferWithNullAmountApproved() {
        loanOffer.setAmountApproved(null);
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(loanOffer));
    }

    @Test
    void saveLoanOfferWithNullDateTimeOffered() {
        loanOffer.setDateTimeOffered(null);
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(loanOffer));
    }

    @Test
    void saveLoanOfferWithNullStatus() {
        loanOffer.setLoanOfferStatus(null);
        assertThrows(MeedlException.class, ()-> loanOfferOutputPort.save(loanOffer));
    }


    @Order(1)
    @Test
    void saveLoanOffer(){
        LoanOffer savedLoanOffer = new LoanOffer();
        try{
            savedLoanOffer = loanOfferOutputPort.save(loanOffer);
        }catch (MeedlException exception){
            log.info("Failed to set up loan offer {}", exception.getMessage());
        }
        assertEquals(savedLoanOffer.getId(), loanOffer.getId());
    }

    @Test
    void findLoanOfferByNullId() {
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.findLoanOfferById(null));
    }

    @Order(2)
    @Test
    void findLoanOfferById(){
        LoanOffer foundLoanOffer = new LoanOffer();
        try{
            foundLoanOffer = loanOfferOutputPort.findLoanOfferById(loanReferralId);
        }catch (MeedlException exception){
            log.info("Failed to find loan Offer {}", exception.getMessage());
        }
        assertEquals(foundLoanOffer.getId(),loanReferralId);
    }


    @Test
    void findAllLoanOfferAssignedToLoaneeWithNullUserId() {
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.findAllLoanOfferAssignedToLoanee(null,pageSize,pageNumber));
    }


    @Order(3)
    @Test
    void findAllLoanOfferAssignedToLOaneeByUserId(){
        Page<LoanOffer> loanOffers = Page.empty();
        try{
            loanOffers = loanOfferOutputPort.findAllLoanOfferAssignedToLoanee(userIdentity.getId(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("Failed to find all loan Offers assigned to loanee {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }


    @Test
    void findAllLoanOfferAssignedToLoaneeInOrganizationWithNullOrganizationId() {
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(null,pageSize,pageNumber));
    }

    @Order(3)
    @Test
    void findAllLoanOfferAssignedToLoaneeInOrganizationByOrganizationId(){
        Page<LoanOffer> loanOffers = Page.empty();
        try{
            loanOffers = loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationIdentity.getId(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("Failed to find all loan Offers assigned to loanee in this organization {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }

    @Order(3)
    @Test
    void findAllLoanOffer(){
        Page<LoanOffer> loanOffers = Page.empty();
        try{
            loanOffers = loanOfferOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationIdentity.getId(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("Failed to find all loan Offered {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }



    @AfterAll
    void cleanUp() throws MeedlException {
        VendorEntity foundGemsVendorEntity = vendorEntityRepository.findByVendorName(loanProduct.getVendors().get(0).getVendorName());
        loanProductVendorRepository.deleteByVendorEntityId((foundGemsVendorEntity.getId()));
        vendorEntityRepository.deleteById(foundGemsVendorEntity.getId());
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