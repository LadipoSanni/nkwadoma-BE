package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductVendorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanProductDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.LoanProductMapper;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanProductServiceTest {
    @InjectMocks
    private LoanProductService loanProductService;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private VendorOutputPort vendorOutputPort;
    @Mock
    private LoanProductMapper loanProductMapper;
    @Mock
    private IdentityManagerOutputPort identityManagerOutPutPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Mock
    private LoanProductVendorOutputPort loanProductVendorOutputPort;
    @Mock
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private Loan loan;
    private LoanProduct loanProduct;
    private Loanee loanee;
    private final String testId = "80123f3b-b8d9-4e7f-876b-df442bfa02c4";
    private InvestmentVehicle investmentVehicle;
    @Mock
    private DisbursementRuleOutputPort disbursementRuleOutputPort;
    @Mock
    private PortfolioOutputPort portfolioOutputPort;
   private List<Vendor> vendors = new ArrayList<>();
    private int pageSize = 10;
    private int pageNumber = 10;
    private Portfolio portfolio;
    private Financier financier;
    private LoanProductDisbursementRule loanProductDisbursementRule;
    private DisbursementRule disbursementRule;

    @BeforeEach
    void setUp() {
        investmentVehicle = InvestmentVehicle.builder()
                .size(new BigDecimal(2000))
                .totalAvailableAmount(new BigDecimal(2000))
                .build();
        loanee = new Loanee();
        loanee.setId("9a4e3b70-3bdb-4676-bcf0-017cd83f6a07");
        loanee.setCohortId("e4fda779-3c21-4dd6-b66a-3a8742f6ecb1");

        financier = Financier.builder().id(UUID.randomUUID().toString()).name("walker").build();
        Vendor vendor = TestData.createTestVendor(TestUtils.generateName(7));
        vendors.add(vendor);
        loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setInvestmentVehicleId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setName("Test Loan Product - unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsor("Mark");
        loanProduct.setTenor(2);
        loanProduct.setMoratorium(2);
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000"));
        loanProduct.setPageSize(10);
        loanProduct.setPageNumber(0);
        loanProduct.setVendors(vendors);
        loanProduct.setSponsors(List.of(financier));
        portfolio = Portfolio.builder().portfolioName(MeedlConstants.MEEDL).build();
//        loanProductDisbursementRule = new LoanProductDisbursementRule();
//        disbursementRule = new DisbursementRule();
//        loanProduct.setDisbursementRule(disbursementRule);


    }
    @Test
    void createLoanProduct() {
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(new UserIdentity());
            when(identityManagerOutPutPort.verifyUserExistsAndIsEnabled(any())).thenReturn(new UserIdentity());
            when(loanProductOutputPort.save(loanProduct)).thenReturn(loanProduct);
//            when(loanProductDisbursementRuleOutputPort.save(any())).thenReturn(loanProductDisbursementRule);

//            when(disbursementRuleOutputPort.save(disbursementRule)).thenReturn(disbursementRule);

            when(vendorOutputPort.saveVendors(anyList())).thenReturn(vendors);
            when(investmentVehicleFinancierOutputPort.checkIfFinancierExistInVehicle(financier.getId(), investmentVehicle.getId())).thenReturn(1);
            when(investmentVehicleOutputPort.findById(loanProduct.getId()))
                    .thenReturn(investmentVehicle);
            when(portfolioOutputPort.findPortfolio(any(Portfolio.class))).thenReturn(portfolio);
            when(portfolioOutputPort.save(any(Portfolio.class))).thenReturn(portfolio);
            LoanProduct createdLoanProduct = loanProductService.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
            assertEquals(createdLoanProduct.getName(), loanProduct.getName());
            verify(loanProductOutputPort, times(1)).save(loanProduct);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
//    @Test
//    public void createLoanProductWithSizeGreaterThanVechicleAvailableAount() {
//        try {
//            when(userIdentityOutputPort.findById(any())).thenReturn(new UserIdentity());
//            when(identityManagerOutPutPort.verifyUserExistsAndIsEnabled(any())).thenReturn(new UserIdentity());
//            investmentVehicle.setTotalAvailableAmount(new BigDecimal(200));
//            when(investmentVehicleOutputPort.findById(loanProduct.getId()))
//                    .thenReturn(investmentVehicle);
//            assertThrows(MeedlException.class , ()-> loanService.createLoanProduct(loanProduct));
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//    }
    @Test
    void createLoanProductWithNullLoanProduct(){
        assertThrows(MeedlException.class, () -> loanProductService.createLoanProduct(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        loanProduct.setMandate(null);
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createLoanProductWithInvalidMandate(String name){
        loanProduct.setMandate(name);
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        loanProduct.setName(null);
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct((loanProduct)));
    }
    @Test
    void createLoanProductWithNegativeLoanProductSize(){
        loanProduct.setLoanProductSize(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }

    @Test
    void createLoanProductWithNoObligorLimit(){
        loanProduct.setObligorLoanLimit(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }

    @Test
    void createLoanProductWithNoTermsAndConditions(){
        loanProduct.setTermsAndCondition(null);
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.EMPTY})
    void createLoanProductWithInvalidTermsAndConditions(String value){
        loanProduct.setTermsAndCondition(value);
        assertThrows(MeedlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.EMPTY})
    void viewLoanProductDetailsWithInvalidId(String value){
        assertThrows(MeedlException.class,()-> loanProductService.viewLoanProductDetailsById(value));
    }
    @Test
    void createLoanProductWithExistingLoanProductName(){
        try {
            UserIdentity userIdentity = new UserIdentity();
            when(userIdentityOutputPort.findById(loanProduct.getCreatedBy())).thenReturn(userIdentity);

            // Corrected line:
            when(identityManagerOutPutPort.verifyUserExistsAndIsEnabled(userIdentity)).thenReturn(userIdentity);

            when(loanProductOutputPort.existsByNameIgnoreCase(loanProduct.getName())).thenReturn(Boolean.TRUE);

            assertThrows(MeedlException.class, () -> loanProductService.createLoanProduct(loanProduct));
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @Test
    void viewLoanProductDetailsWithValidId(){
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(loanProductVendorOutputPort.getVendorsByLoanProductId(loanProduct.getId())).thenReturn(vendors);
            LoanProduct foundLoanProduct = loanProductService.viewLoanProductDetailsById(loanProduct.getId());
            assertEquals(foundLoanProduct.getName() , loanProduct.getName());
            assertEquals(foundLoanProduct.getMandate() , loanProduct.getMandate());
            verify(loanProductOutputPort, times(1)).findById(loanProduct.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"non-existing loan product", StringUtils.SPACE, StringUtils.EMPTY })
    void updateByIdWithAndInvalidId(String id) {
        loanProduct.setId(id);
        assertThrows(MeedlException.class , ()-> loanProductService.updateLoanProduct(loanProduct));
    }
    @Test
    void updateLoanProduct(){

        loanProduct.setDisbursementTerms("Updated Gemini Loan Product");
        loanProduct.setId(testId);
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(loanProductMapper.updateLoanProduct(loanProduct, loanProduct)).thenReturn(loanProduct);
//            when(investmentVehicleOutputPort.findById(anyString())).thenReturn(investmentVehicle);
            loanProduct = loanProductService.updateLoanProduct(loanProduct);
            LoanProduct updatedLoanProduct = loanProductOutputPort.findById(testId);
            assertNotNull(updatedLoanProduct);
            assertEquals("Updated Gemini Loan Product", updatedLoanProduct.getDisbursementTerms());
        } catch (MeedlException e) {
            log.error("Failed to update loan product {}", e.getMessage());
        }
    }
    @Test
    void updateByIdWithNull() {
        assertThrows(MeedlException.class , ()-> loanProductService.updateLoanProduct(null));
    }
    @Test
    void deleteLoanProductWithNullRequest(){
        assertThrows(MeedlException.class, ()-> loanProductService.deleteLoanProductById(null));
    }

    @Test
    void viewAllPrograms() {
        Page<LoanProduct> expectedPage = new PageImpl<>(Collections.singletonList(loanProduct), PageRequest.of(loanProduct.getPageNumber(), loanProduct.getPageSize()), 1);
            when(loanProductService.viewAllLoanProduct( loanProduct)).
                    thenReturn(new PageImpl<>(List.of(loanProduct)));
            Page<LoanProduct> loanProductPage = loanProductService.viewAllLoanProduct(loanProduct);
            List<LoanProduct> loanProductList = loanProductPage.toList();

            assertNotNull(loanProductPage);
            assertNotNull(loanProductList);
            assertEquals(loanProductList.get(0).getMandate(), loanProduct.getMandate());
            assertEquals(loanProductList.get(0).getName(), loanProduct.getName());
            assertEquals(loanProductList.get(0).getTermsAndCondition(), loanProduct.getTermsAndCondition());

    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY })
    void searchForLoanProductWithInvalidName(String name) {
        loanProduct.setName(name);
        assertThrows(MeedlException.class, ()-> loanProductService.search(loanProduct.getName(),pageSize,pageNumber));
    }
    @Test
    void searchLoanProduct() {
        Page<LoanProduct> loanProducts = Page.empty();
        try{
            when(loanProductOutputPort.search(loanProduct.getName(),pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(List.of(loanProduct, loanProduct)));

            loanProducts = loanProductService.search(loanProduct.getName(),pageSize,pageNumber);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

        assertNotNull(loanProducts);
        assertEquals(2, loanProducts.getContent().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteLoanProductWithInvalidId(String value){
        loanProduct.setId(value);
        assertThrows(MeedlException.class, ()-> loanProductService.deleteLoanProductById(loanProduct));
    }
    @Test
    void deleteLoanProductWithValidId(){
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(investmentVehicleOutputPort.findById(anyString())).thenReturn(investmentVehicle);
            when(investmentVehicleOutputPort.save(investmentVehicle)).thenReturn(investmentVehicle);
            doNothing().when(loanProductOutputPort).deleteById(loanProduct.getId());
            loanProductService.deleteLoanProductById(loanProduct);
            verify(loanProductOutputPort, times(1)).deleteById(loanProduct.getId());
        } catch (MeedlException e) {
            log.error("Error deleting loan product {}", e.getMessage());
        }
    }

}
