package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.Product;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private LoanProductVendorRepository loanProductVendorRepository;
    @Autowired
    private VendorEntityRepository vendorEntityRepository;
    private LoanProduct gemsLoanProduct;
    private Vendor vendor;
    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setVendorName("Test Vendor");
        vendor.setTermsAndConditions("Test: A new vendor for test with terms and condition imaginary");
        vendor.setProduct(Product.ACCOMMODATION);

        gemsLoanProduct = new LoanProduct();
        gemsLoanProduct.setPageSize(10);
        gemsLoanProduct.setPageNumber(0);
        gemsLoanProduct.setName("Test Loan Product 2");
        gemsLoanProduct.setMandate("Test: A new mandate for test");
        gemsLoanProduct.setSponsors(List.of("Mark", "Jack"));
        gemsLoanProduct.setLoanProductSize(new BigDecimal("1000.00"));
        gemsLoanProduct.setObligorLoanLimit(new BigDecimal("1000.00"));
        gemsLoanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        gemsLoanProduct.setVendors(List.of(vendor));
    }

    @Test
    @Order(1)
    void createLoanProduct() {
        try {
            LoanProduct createdLoanProduct = loanProductOutputPort.save(gemsLoanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(createdLoanProduct.getId());
            assertNotNull(foundLoanProduct);
            foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            assertNotNull(foundLoanProduct);
            assertEquals(foundLoanProduct.getName(),gemsLoanProduct.getName());
            assertEquals(foundLoanProduct.getTermsAndCondition(), gemsLoanProduct.getTermsAndCondition());
            assertNotNull(createdLoanProduct.getVendors());
            assertEquals(createdLoanProduct.getVendors().get(0).getVendorName(), gemsLoanProduct.getVendors().get(0).getVendorName());
            assertNotNull(createdLoanProduct.getVendors().get(0).getId());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @Test
    @Order(2)
    void createLoanProductWithExistingLoanProductName(){
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            assertNotNull(foundLoanProduct);
            assertEquals(foundLoanProduct.getName(),gemsLoanProduct.getName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class,()-> loanProductOutputPort.save(gemsLoanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        gemsLoanProduct.setName(null);
        assertThrows(MeedlException.class,()-> loanProductOutputPort.save((gemsLoanProduct)));
    }
    @Test
    void createLoanProductWithNullLoanProduct(){
        assertThrows(MeedlException.class, () -> loanProductOutputPort.save(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        gemsLoanProduct.setMandate(null);
        assertThrows(MeedlException.class,()-> loanProductOutputPort.save(gemsLoanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createLoanProductWithInvalidMandate(String name){
        gemsLoanProduct.setMandate(name);
        assertThrows(MeedlException.class,()-> loanProductOutputPort.save(gemsLoanProduct));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void createWithInvalidProductSize(String size){
        gemsLoanProduct.setLoanProductSize(new BigDecimal(size));
        assertThrows(MeedlException.class, () -> loanProductOutputPort.save(gemsLoanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void createWithInvalidObligorLimit(String obligorLimit){
        gemsLoanProduct.setObligorLoanLimit(new BigDecimal(obligorLimit));
        assertThrows(MeedlException.class, () -> loanProductOutputPort.save(gemsLoanProduct));
    }
    @Test
    void createWithNullLoanRequest(){
        assertThrows(MeedlException.class, () -> loanProductOutputPort.save(null));
    }

    @Test
    void createLoanProductWithNoTermsAndConditions(){
        gemsLoanProduct.setTermsAndCondition(null);
        assertThrows(MeedlException.class,()-> loanProductOutputPort.save(gemsLoanProduct));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void existsByInvalidName(String name) {
        gemsLoanProduct.setName(name);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        gemsLoanProduct.setName(null);
        assertThrows(MeedlException.class, () -> loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
    }
    @ParameterizedTest
    @ValueSource(strings = {"non-existing loan product"})
    void existsByNameWithNonExistingName(String name) {
        gemsLoanProduct.setName(name);
        try {
            assertFalse(loanProductOutputPort.existsByName(gemsLoanProduct.getName()));
        } catch (MeedlException exception) {
            log.error("existsByNameFalse method failed to check if exist: {}",exception.getMessage());
        }
    }
    @Test
    @Order(3)
    void existsByNameWithValidName() {
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
    @Order(4)
    void findByName(){
        LoanProduct foundLoanProduct = null;
        try {
            foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
        } catch (MeedlException e) {
            log.error("Failed to find loan product by name: {} With exception : {} {}", gemsLoanProduct.getName(), e.getClass().getName(), e.getMessage());
        }
        assertNotNull(foundLoanProduct);
        assertNotNull(foundLoanProduct.getId());
        assertEquals(gemsLoanProduct.getMandate(), foundLoanProduct.getMandate());
    }
    @Test
    @Order(5)
    void findById(){
        LoanProduct foundLoanProduct = null;
        try {
            foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            assertNotNull(foundLoanProduct);
            assertNotNull(foundLoanProduct.getId());
            foundLoanProduct = loanProductOutputPort.findById(foundLoanProduct.getId());
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

    @Test
    @Order(6)
    void findAllLoanProduct() {
            Page<LoanProduct> foundLoanProducts = loanProductOutputPort.findAllLoanProduct(gemsLoanProduct);
            List<LoanProduct> loanProductList = foundLoanProducts.toList();

            assertEquals(1, foundLoanProducts.getTotalElements());
            assertEquals(1, foundLoanProducts.getTotalPages());
            assertTrue(foundLoanProducts.isFirst());
            assertTrue(foundLoanProducts.isLast());

            assertNotNull(loanProductList);
            assertEquals(1, loanProductList.size());
            assertEquals(gemsLoanProduct.getName(), loanProductList.get(0).getName());
            assertEquals(gemsLoanProduct.getTermsAndCondition(), loanProductList.get(0).getTermsAndCondition());
            assertEquals(gemsLoanProduct.getObligorLoanLimit(), loanProductList.get(0).getObligorLoanLimit());

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
    void updateLoanProduct(){
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            foundLoanProduct.setDisbursementTerms("Updated Gemini Loan Product");
            LoanProduct loanProduct = loanProductOutputPort.save(foundLoanProduct);
            LoanProduct updatedLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
            assertNotNull(updatedLoanProduct);
            assertEquals("Updated Gemini Loan Product", updatedLoanProduct.getDisbursementTerms());
        } catch (MeedlException e) {
            log.error("Failed to update loan product");
        }
    }
    @Test
    @Order(7)
    void deleteLoanProduct() {
        try {
            LoanProduct foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            assertNotNull(foundLoanProduct);
            assertNotNull(foundLoanProduct.getId());
            assertEquals(gemsLoanProduct.getName(), foundLoanProduct.getName());
            loanProductOutputPort.deleteById(foundLoanProduct.getId());

            assertThrows(MeedlException.class, ()->loanProductOutputPort.findById(foundLoanProduct.getId()));
        } catch (MeedlException e) {
            log.error("Failed to delete loan product {}", gemsLoanProduct.getName());
        }
    }

    @AfterAll
    void cleanUp() {
        try {
            VendorEntity foundVendorEntity = vendorEntityRepository.findByVendorName(vendor.getVendorName());
            loanProductVendorRepository.deleteAllByVendorEntity(foundVendorEntity);
            vendorEntityRepository.deleteById(foundVendorEntity.getId());
            LoanProduct foundLoanProduct = loanProductOutputPort.findByName(gemsLoanProduct.getName());
            loanProductOutputPort.deleteById(foundLoanProduct.getId());
        } catch (MeedlException e) {
            log.error("Failed to delete loan product {}", gemsLoanProduct.getName());
        }
    }
}