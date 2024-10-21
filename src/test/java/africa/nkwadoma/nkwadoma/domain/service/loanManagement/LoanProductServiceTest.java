package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Months;
import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Years;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanProductServiceTest {
    @Mock
    private LoanProductOutputPort loanProductOutputPort;

    @InjectMocks
    private LoanService loanService;

    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        loanProduct = new LoanProduct();
        loanProduct.setName("Test Loan Product: unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000"));
        loanProduct.setId("uuid.idfortesting");
    }

    @Test
    void createLoanProduct() {
        try {
            when(loanProductOutputPort.save(loanProduct)).thenReturn(loanProduct);
            LoanProduct createdLoanProduct = loanService.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
            assertEquals(createdLoanProduct.getName(), loanProduct.getName());
            verify(loanProductOutputPort, atLeastOnce()).save(loanProduct);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
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
    @Test
    void deleteLoanProductWithNullRequest(){
        assertThrows(MeedlException.class, ()-> loanService.deleteLoanProductById(null));
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
            verify(loanProductOutputPort, atLeastOnce()).deleteById(loanProduct.getId());
        } catch (MeedlException e) {
            log.error("Error deleting loan product {}", e.getMessage());
        }
    }
}