package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanProductServiceTest {
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private LoanProductMapper loanProductMapper;
    @Mock
    private IdentityManagerOutputPort identityManagerOutPutPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private LoanOutputPort loanOutputPort;
    @Mock
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @InjectMocks
    private LoanService loanService;
    private Loan loan;
    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        Vendor vendor = new Vendor();
        loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setInvestmentVehicleId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setName("Test Loan Product - unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsor("Mark");
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000"));
        loanProduct.setPageSize(10);
        loanProduct.setPageNumber(0);
        loanProduct.setVendors(List.of(vendor));


    }
    @Test
    void createLoanProduct() {
        try {
            when(loanProductOutputPort.save(loanProduct)).thenReturn(loanProduct);
            when(userIdentityOutputPort.findById(any())).thenReturn(new UserIdentity());
            when(identityManagerOutPutPort.verifyUserExistsAndIsEnabled(any())).thenReturn(new UserIdentity());
            when(investmentVehicleOutputPort.findById(loanProduct.getId()))
                    .thenReturn(new InvestmentVehicle());
            LoanProduct createdLoanProduct = loanService.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
            assertEquals(createdLoanProduct.getName(), loanProduct.getName());
            verify(loanProductOutputPort, times(1)).save(loanProduct);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
    @Test
    void createLoanProductWithNullLoanProduct(){
        assertThrows(MeedlException.class, () -> loanService.createLoanProduct(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        loanProduct.setMandate(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createLoanProductWithInvalidMandate(String name){
        loanProduct.setMandate(name);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        loanProduct.setName(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct((loanProduct)));
    }
    @Test
    void createLoanProductWithNegativeLoanProductSize(){
        loanProduct.setLoanProductSize(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoObligorLimit(){
        loanProduct.setObligorLoanLimit(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoTermsAndConditions(){
        loanProduct.setTermsAndCondition(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.EMPTY})
    void createLoanProductWithInvalidTermsAndConditions(String value){
        loanProduct.setTermsAndCondition(value);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.EMPTY})
    void viewLoanProductDetailsWithInvalidId(String value){
        assertThrows(MeedlException.class,()-> loanService.viewLoanProductDetailsById(value));
    }
    @Test
    void viewLoanProductDetailsWithValidId(){
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            LoanProduct foundLoanProduct = loanService.viewLoanProductDetailsById(loanProduct.getId());
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
        assertThrows(MeedlException.class , ()->loanService.updateLoanProduct(loanProduct));
    }
    @Test
    void updateLoanProduct(){

        loanProduct.setDisbursementTerms("Updated Gemini Loan Product");
        loanProduct.setId("80123f3b-b8d9-4e7f-876b-df442bfa02c4");
        try {
            when(loanProductOutputPort.save(loanProduct)).thenReturn(loanProduct);
            when(loanProductMapper.updateLoanProduct(any(), any())).thenReturn(loanProduct);
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            loanProduct = loanService.updateLoanProduct(loanProduct);
            LoanProduct updatedLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
            assertNotNull(updatedLoanProduct);
            assertEquals("Updated Gemini Loan Product", updatedLoanProduct.getDisbursementTerms());
        } catch (MeedlException e) {
            log.error("Failed to update loan product {}", e.getMessage());
        }
    }
    @Test
    void updateByIdWithNull() {
        assertThrows(MeedlException.class , ()->loanService.updateLoanProduct(null));
    }
    @Test
    void deleteLoanProductWithNullRequest(){
        assertThrows(MeedlException.class, ()-> loanService.deleteLoanProductById(null));
    }

    @Test
    void viewAllPrograms() {
        Page<LoanProduct> expectedPage = new PageImpl<>(Collections.singletonList(loanProduct), PageRequest.of(loanProduct.getPageNumber(), loanProduct.getPageSize()), 1);
            when(loanService.viewAllLoanProduct( loanProduct)).
                    thenReturn(new PageImpl<>(List.of(loanProduct)));
            Page<LoanProduct> loanProductPage = loanService.viewAllLoanProduct(loanProduct);
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
        assertThrows(MeedlException.class, ()-> loanService.search(loanProduct.getName()));
    }
    @Test
    void searchLoanProduct() {
        List<LoanProduct> loanProducts = new ArrayList<>();
        try{
            when(loanProductOutputPort.search(loanProduct.getName()))
                    .thenReturn(List.of(loanProduct, loanProduct));

            loanProducts = loanService.search(loanProduct.getName());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

        assertNotNull(loanProducts);
        assertEquals(2, loanProducts.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteLoanProductWithInvalidId(String value){
        loanProduct.setId(value);
        assertThrows(MeedlException.class, ()-> loanService.deleteLoanProductById(loanProduct));
    }
    @Test
    void deleteLoanProductWithValidId(){
        try {
            doNothing().when(loanProductOutputPort).deleteById(loanProduct.getId());
            loanService.deleteLoanProductById(loanProduct);
            verify(loanProductOutputPort, times(1)).deleteById(loanProduct.getId());
        } catch (MeedlException e) {
            log.error("Error deleting loan product {}", e.getMessage());
        }
    }

    @Test
    void viewLoanDetailsWithValidId(){
        loan = new Loan();
        loan.setId("4dced61b-acff-4487-87f7-587977fd146a");
        try {
            when(loanOutputPort.findLoanById(loan.getId())).thenReturn(loan);
            Loan foundLoan = loanService.viewLoanDetails(loan.getId());

            assertNotNull(foundLoan.getId());

            verify(loanOutputPort, times(1)).findLoanById(loan.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.EMPTY})
    void viewLoanDetailsWithInvalidId(String loanId){
        assertThrows(MeedlException.class,()-> loanService.viewLoanDetails(loanId));
    }
}
