package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Months;
import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Years;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoanProductServiceTest {
    @Autowired
    private CreateLoanProductUseCase createLoanProductUseCase;
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
    }

    @Test
    @Order(1)
    void createLoanProduct() {
        LoanProduct createdLoanProduct = null;
        try {
            createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            log.info(createdLoanProduct.getId());
            assertNotNull(createdLoanProduct.getId());
            createLoanProductUseCase.deleteLoanProductById(createdLoanProduct);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
    }
    @Test
    void createLoanProductWithTheSameName(){
        loanProduct.setName("Test: Similar test name only for application testing in code");
        LoanProduct createdLoanProduct = null;
        try {
            createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
            assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
            createLoanProductUseCase.deleteLoanProductById(createdLoanProduct);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }
    @Test
    void createLoanProductWithNullLoanProduct(){
        assertThrows(MeedlException.class, () -> createLoanProductUseCase.createLoanProduct(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        loanProduct.setMandate(null);
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createLoanProductWithInvalidMandate(String name){
        loanProduct.setMandate(name);
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        loanProduct.setName(null);
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct((loanProduct)));
    }
    @Test
    void createLoanProductWithNegativeLoanProductSize(){
        loanProduct.setLoanProductSize(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoObligorLimit(){
        loanProduct.setObligorLoanLimit(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoTermsAndConditions(){
        loanProduct.setTermsAndCondition(null);
        assertThrows(MeedlException.class,()-> createLoanProductUseCase.createLoanProduct(loanProduct));
    }
    @Test
    void deleteLoanProductWithNullRequest(){
        assertThrows(MeedlException.class, ()-> createLoanProductUseCase.deleteLoanProductById(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteLoanProductWithNullId(String value){
        loanProduct.setId(value);
        assertThrows(MeedlException.class, ()-> createLoanProductUseCase.deleteLoanProductById(loanProduct));
        loanProduct.setId(null);
        assertThrows(MeedlException.class, ()-> createLoanProductUseCase.deleteLoanProductById(loanProduct));
    }
}