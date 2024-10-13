package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private String gemsLoanProductId;

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
        assertThrows(MeedlException.class,()-> loanProductOutputPort.findById(gemsLoanProduct.getId()));
            LoanProduct createdLoanProduct = loanProductOutputPort.save(gemsLoanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
            gemsLoanProductId = createdLoanProduct.getId();
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(createdLoanProduct.getId());
            assertNotNull(foundLoanProduct);
            assertEquals(foundLoanProduct.getName(),gemsLoanProduct.getName());
            assertEquals(foundLoanProduct.getTermsAndCondition(), gemsLoanProduct.getTermsAndCondition());
            gemsLoanProduct.setId(createdLoanProduct.getId());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"-1", "1000", StringUtils.EMPTY, StringUtils.SPACE})
    void createWithInvalidProductSize(String size){
        gemsLoanProduct.setLoanProductSize(new BigDecimal(size));
        assertThrows(MeedlException.class, () -> loanProductOutputPort.save(gemsLoanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "fake/non-existing test" })
    void existsByNameFalse(String name) {
        gemsLoanProduct.setName(name);
        try {
            assertFalse(loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        } catch (MeedlException exception) {
           log.error("existsByNameFalse method failed to check if exist: {}",exception.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " "})
    void existsByInvalidName(String name) {
        gemsLoanProduct.setName(name);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        gemsLoanProduct.setName(null);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
    }
    @Test
    @Order(2)
    void existsByNameTrue() {
        try {
        assertTrue(loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " "})
    void findByInvalidName(String name){
        gemsLoanProduct.setName(name);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.findByName(gemsLoanProduct.getName()));
        gemsLoanProduct.setName(null);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.findByName(gemsLoanProduct.getName()));
    }
    @Test
    @Order(3)
    void findByName(){
        LoanProduct foundLoanProduct = null;
        try {
            foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
        } catch (MeedlException e) {
            log.error("Failed to find loan product by name: {} With exception : {} {}", gemsLoanProduct.getName(), e.getClass().getName(), e.getMessage());
        }
        assertNotNull(foundLoanProduct);
    }
    @Test
    @Order(4)
    void findById(){
        LoanProduct foundLoanProduct = null;
        try {
            foundLoanProduct = loanProductOutputPort.findById(gemsLoanProductId);
        } catch (MeedlException e) {
            log.error("Failed to find loan product by id: {} With exception : {} {}",gemsLoanProduct.getId(), e.getClass().getName(), e.getMessage());
        }
        assertNotNull(foundLoanProduct);
    }
    @Test
    void findByNullId(){
        assertThrows(MeedlException.class, () -> loanProductOutputPort.findById(null));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " "})
    void findByInvalidId(String id) {
        gemsLoanProduct.setId(id);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.findById(gemsLoanProduct.getId()));
    }


    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void deleteWithEmptyId(String id) {
        gemsLoanProduct.setId(id);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.deleteById(gemsLoanProduct.getId()));
        gemsLoanProduct.setId(null);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.deleteById(gemsLoanProduct.getId()));
    }
    @Test
    @Order(5)
    void deleteLoanProduct() {
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(gemsLoanProductId);
            assertNotNull(foundLoanProduct);
            loanProductOutputPort.deleteById(gemsLoanProductId);
        } catch (MeedlException e) {
            log.error("Failed to delete loan product {}", gemsLoanProductId);
            assertTrue(false);
        }
        assertThrows(MeedlException.class, ()->loanProductOutputPort.findById(gemsLoanProduct.getId()));
    }
}