package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class DisbursementRuleAdapterTest {
    @Autowired
    private DisbursementRuleOutputPort disbursementRuleOutputPort;

    @Autowired
    private DisbursementRuleRepository disbursementRuleRepository;
    private String disbursementRuleId;
    private int pageSize = 10;
    private int pageNumber = 0;
    private DisbursementRule disbursementRule;


    @BeforeAll
    void setUpLoanOffer() {
        disbursementRule = new DisbursementRule();
    }


    @Test
    void saveNullDisbursementRule() {
        assertThrows(MeedlException.class, ()-> disbursementRuleOutputPort.save(null));
    }


    @Order(1)
    @Test
    void saveDisbursementRule(){
        DisbursementRule savedDisbursementRule = null;
        try{
            savedDisbursementRule = disbursementRuleOutputPort.save(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to set up loan offer {}", exception.getMessage());
        }
        assertNotNull(savedDisbursementRule);
        assertEquals(savedDisbursementRule.getId(), disbursementRule.getId());
        disbursementRuleId = savedDisbursementRule.getId();
    }

    @Test
    void findDisbursementRuleByNullId() {
        assertThrows(MeedlException.class, () -> disbursementRuleOutputPort.findById(null));
    }

    @Order(2)
    @Test
    void findLoanOfferById(){
        DisbursementRule foundDisbursementRule = null;
        try{
            foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRuleId);
        }catch (MeedlException exception){
            log.info("Failed to find loan Offer {}", exception.getMessage());
        }
        assertEquals(foundDisbursementRule.getId(),loanReferralId);
    }


    @Test
    void findAllLoanOfferAssignedToLoaneeWithNullUserId() {
        assertThrows(MeedlException.class, () -> disbursementRuleOutputPort.findAllLoanOfferAssignedToLoanee(null,pageSize,pageNumber));
    }


    @Order(3)
    @Test
    void findAllLoanOfferAssignedToLOaneeByUserId(){
        Page<LoanOffer> loanOffers = Page.empty();
        try{
            loanOffers = disbursementRuleOutputPort.findAllLoanOfferAssignedToLoanee(userIdentity.getId(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("Failed to find all loan Offers assigned to loanee {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }


    @Test
    void findAllLoanOfferAssignedToLoaneeInOrganizationWithNullOrganizationId() {
        assertThrows(MeedlException.class, () -> disbursementRuleOutputPort.findAllLoanOfferedToLoaneesInOrganization(null,pageSize,pageNumber));
    }

    @Order(3)
    @Test
    void findAllLoanOfferAssignedToLoaneeInOrganizationByOrganizationId(){
        Page<LoanOffer> loanOffers = Page.empty();
        try{
            loanOffers = disbursementRuleOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationIdentity.getId(),pageSize,pageNumber);
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
            loanOffers = disbursementRuleOutputPort.findAllLoanOfferedToLoaneesInOrganization(organizationIdentity.getId(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.info("Failed to find all loan Offered {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }


    @Order(4)
    @Test
    void searchLoanOffer() {
        disbursementRule.setPageNumber(pageNumber);
        disbursementRule.setPageSize(pageSize);
        disbursementRule.setName("john");
        Page<LoanOffer> loanOffers = Page.empty();
        try {
            loanOffers = disbursementRuleOutputPort.searchLoanOffer(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to search loan Offer {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }

    @Order(5)
    @Test
    void searchLoanOfferByOrganization() {
        disbursementRule.setPageNumber(pageNumber);
        disbursementRule.setPageSize(pageSize);
        disbursementRule.setName("john");
        disbursementRule.setOrganizationId(organizationIdentity.getId());
        Page<LoanOffer> loanOffers = Page.empty();
        try {
            loanOffers = disbursementRuleOutputPort.searchLoanOffer(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to search loan Offer {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }

    @Order(6)
    @Test
    void searchLoanOfferByOrganizationIdAndProgramId() {
        disbursementRule.setPageNumber(pageNumber);
        disbursementRule.setPageSize(pageSize);
        disbursementRule.setName("john");
        disbursementRule.setOrganizationId(organizationIdentity.getId());
        disbursementRule.setProgramId(program.getId());
        Page<LoanOffer> loanOffers = Page.empty();
        try {
            loanOffers = disbursementRuleOutputPort.searchLoanOffer(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to search loan Offer {}", exception.getMessage());
        }
        assertEquals(1,loanOffers.getTotalElements());
    }

    @Order(7)
    @Test
    void searchLoanOfferByWrongOrganizationId() {
        disbursementRule.setPageNumber(pageNumber);
        disbursementRule.setPageSize(pageSize);
        disbursementRule.setName("john");
        disbursementRule.setOrganizationId(program.getId());
        Page<LoanOffer> loanOffers = Page.empty();
        try {
            loanOffers = disbursementRuleOutputPort.searchLoanOffer(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to search loan Offer {}", exception.getMessage());
        }
        assertEquals(0,loanOffers.getTotalElements());
    }



    @AfterAll
    void cleanUp() throws MeedlException {
        VendorEntity foundGemsVendorEntity = vendorEntityRepository.findByVendorName(loanProduct.getVendors().get(0).getVendorName());
        disbursementRuleRepository.deleteByVendorEntityId((foundGemsVendorEntity.getId()));
        vendorEntityRepository.deleteById(foundGemsVendorEntity.getId());
        disbursementRuleOutputPort.deleteLoanOfferById(loanReferralId);
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
