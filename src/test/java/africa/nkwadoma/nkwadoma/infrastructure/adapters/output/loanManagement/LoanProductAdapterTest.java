package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoanProductAdapterTest {
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;
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
            LoanProduct createdLoanProduct = loanProductOutputPort.save(loanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
        try {
            loanProductOutputPort.deleteById(createdLoanProduct.getId());
        } catch (MiddlException e) {
            log.error("Failed to delete {}", createdLoanProduct.getId());
            assertTrue(false);
        }
    }
    @Test
    void existsByNameFalse() {
        loanProduct.setName("fake/non-existing test");
        try {
            assertFalse(loanProductOutputPort.existsByName(loanProduct.getName()));
        } catch (MiddlException exception) {
           log.error("existsByNameFalse method failed to check if exist: {}",exception.getMessage());
           assertTrue(false);
        }
    }

    @Test
    void existsByNullName() {
        loanProduct.setName(null);
        assertThrows(MiddlException.class, () -> loanProductOutputPort.existsByName(loanProduct.getName()));
    }
    @Test
    void existsByNameTrue() {
        loanProduct.setName("true test ");
        LoanProduct savedLoanProduct = loanProductOutputPort.save(loanProduct);
        try {
        assertTrue(loanProductOutputPort.existsByName(loanProduct.getName()));
        loanProductOutputPort.deleteById(savedLoanProduct.getId());
        } catch (MiddlException exception) {
            log.error(exception.getMessage());
            assertTrue(false);
        }
    }
    @Test
    void deleteLoanProduct() {
        loanProduct.setName("Test: for delete loan product. Application testing in code");
        LoanProduct savedLoanProduct = loanProductOutputPort.save(loanProduct);
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
            assertNotNull(foundLoanProduct);
            loanProductOutputPort.deleteById(savedLoanProduct.getId());
        } catch (MiddlException e) {
            log.error("Failed to delete loan product {}", savedLoanProduct.getId());
            assertTrue(false);
        }
        assertThrows(MiddlException.class, ()->loanProductOutputPort.findById(loanProduct.getId()));
    }
    @Test
    void deleteWithNullId() {
        loanProduct.setId(null);
        assertThrows(MiddlException.class, () -> loanProductOutputPort.deleteById(loanProduct.getId()));
    }

    @Test
    void findById(){
        try {
            loanProduct.setName("find by id test: Success test... ");
            LoanProduct createdLoanProduct = loanProductOutputPort.save(loanProduct);
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(createdLoanProduct.getId());
            assertNotNull(foundLoanProduct);
            loanProductOutputPort.deleteById(foundLoanProduct.getId());
        } catch (MiddlException e) {
            log.error("Failed to find loan product by id ");
            assertTrue(false);
        }
    }
    @Test
    void findByIdWithNullI(){
        assertThrows(MiddlException.class, () -> loanProductOutputPort.findById(null));
    }
}