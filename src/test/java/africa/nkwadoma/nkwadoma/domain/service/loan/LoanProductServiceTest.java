package africa.nkwadoma.nkwadoma.domain.service.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoanProductServiceTest {
    @Autowired
    private LoanService loanProductService;
    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        loanProduct = new LoanProduct();
        loanProduct.setName("Test Loan Product");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setLoanProductSize(new BigDecimal(1000));
        loanProduct.setObligorLoanLimit(new BigDecimal(1000));
        loanProduct.setInterestRate(0);
        loanProduct.setMoratorium(5);
        loanProduct.setTenor(5);
        loanProduct.setMinRepaymentAmount(new BigDecimal(1000));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
    }

    @Test
    void createLoanProduct() {
        try {
            LoanProduct createdLoanProduct = loanProductService.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            log.info(createdLoanProduct.getId());
            assertNotNull(createdLoanProduct.getId());
        }catch (MiddlException exception){
            log.error(exception.getMessage());
        }
    }

    @Test
    void createLoanProductWithNullRequestEntity(){
        assertThrows(MiddlException.class, () -> loanProductService.createLoanProduct(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        loanProduct.setMandate(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        loanProduct.setName(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct((loanProduct)));
    }
    @Test
    void createLoanProductWithNullSponsor(){
        loanProduct.setSponsors(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoSponsor(){
        loanProduct.setSponsors(new ArrayList<>());
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductSize(){
        loanProduct.setLoanProductSize(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNegativeLoanProductSize(){
        loanProduct.setLoanProductSize(new BigDecimal(-1));
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void obligorLimitGreaterThanLoanProductSize(){
        assertNotNull(null);
    }
    @Test
    void createLoanProductWithNullObligorLimit(){
        loanProduct.setObligorLoanLimit(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoObligorLimit(){
        loanProduct.setObligorLoanLimit(new BigDecimal(-1));
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNegativeInterestRate(){
        loanProduct.setInterestRate(-1);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNegativeMoratoriumPeriod(){
        loanProduct.setMoratorium(-1);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNegativeTenor(){
        loanProduct.setTenor(-1);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullMinimumRepaymentAmount(){
        loanProduct.setMinRepaymentAmount(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoTermsAndConditions(){
        loanProduct.setTermsAndCondition(null);
        assertThrows(MiddlException.class,()-> loanProductService.createLoanProduct(loanProduct));
    }
}