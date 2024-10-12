package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanProductAdapterTest {
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;
    private LoanProduct gemsLoanProduct;

    @BeforeEach
    void setUp() {
        gemsLoanProduct = new LoanProduct();
        gemsLoanProduct.setName("Test Loan Product");
        gemsLoanProduct.setMandate("Test: A new mandate for test");
        gemsLoanProduct.setSponsors(List.of("Mark", "Jack"));
        gemsLoanProduct.setLoanProductSize(new BigDecimal(1000));
        gemsLoanProduct.setObligorLoanLimit(new BigDecimal(1000));
        gemsLoanProduct.setInterestRate(0);
        gemsLoanProduct.setMoratorium(5);
        gemsLoanProduct.setTenor(5);
        gemsLoanProduct.setMinRepaymentAmount(new BigDecimal(1000));
        gemsLoanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
    }

    @Test
    @Order(1)
    void createLoanProduct() {
        assertThrows(LoanException.class,()-> loanProductOutputPort.findById(gemsLoanProduct.getId()));
            LoanProduct createdLoanProduct = loanProductOutputPort.save(gemsLoanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(createdLoanProduct.getId());
            assertNotNull(foundLoanProduct);
            assertEquals(foundLoanProduct.getName(),gemsLoanProduct.getName());
            assertEquals(foundLoanProduct.getTermsAndCondition(), gemsLoanProduct.getTermsAndCondition());
            gemsLoanProduct.setId(createdLoanProduct.getId());
        } catch (MiddlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @Test
    void existsByNameFalse() {
        log.info(gemsLoanProduct.getId());
        gemsLoanProduct.setName("fake/non-existing test");
        try {
            assertFalse(loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        } catch (MiddlException exception) {
           log.error("existsByNameFalse method failed to check if exist: {}",exception.getMessage());
        }
    }

    @Test
    void existsByNullName() {
        gemsLoanProduct.setName(null);
        assertThrows(MiddlException.class, () -> loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
    }
    @Test
    @Order(2)
    void existsByNameTrue() {
        try {
        assertTrue(loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        } catch (MiddlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @Test
    @Order(3)
    void findById(){
        updateGemsLoanProductId();
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(gemsLoanProduct.getId());
            assertNotNull(foundLoanProduct);
        } catch (MiddlException e) {
            log.error("Failed to find loan product by id ");
            assertTrue(false);
        }
    }
    @Test
    void findByNullId(){
        assertThrows(MiddlException.class, () -> loanProductOutputPort.findById(null));
    }
    @Test
    void deleteWithNullId() {
        gemsLoanProduct.setId(null);
        assertThrows(MiddlException.class, () -> loanProductOutputPort.deleteById(gemsLoanProduct.getId()));
    }
    @Test
    @Order(4)
    void deleteLoanProduct() {
        updateGemsLoanProductId();
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(gemsLoanProduct.getId());
            assertNotNull(foundLoanProduct);
            loanProductOutputPort.deleteById(gemsLoanProduct.getId());
        } catch (MiddlException e) {
            log.error("Failed to delete loan product {}", gemsLoanProduct.getId());
            assertTrue(false);
        }
        assertThrows(MiddlException.class, ()->loanProductOutputPort.findById(gemsLoanProduct.getId()));
    }
    private void updateGemsLoanProductId(){
        try {
            gemsLoanProduct.setId(loanProductOutputPort.findByName(gemsLoanProduct.getName()).getId());
        } catch (LoanException e) {
            throw new RuntimeException(e);
        }
    }
}