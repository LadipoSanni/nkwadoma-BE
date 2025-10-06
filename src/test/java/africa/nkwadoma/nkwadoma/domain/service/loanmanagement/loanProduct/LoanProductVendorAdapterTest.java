package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductVendor;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct.LoanProductVendorAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanProductVendorAdapterTest {

    @Autowired
    private LoanProductVendorAdapter loanProductVendorAdapter;

    @Autowired
    private LoanProductVendorRepository loanProductVendorRepository;

    private String junoLoanProductVendorId;

    private LoanProduct loanProduct;
    private Vendor vendorJuno;
    private String vendorJunoId;
    private String loanProductId;
    @Autowired
    private VendorOutputPort vendorOutputPort;
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;

    @BeforeAll
    void setup() throws MeedlException {
        loanProduct = TestData.buildTestLoanProduct();
        vendorJuno = TestData.createTestVendor("Juno");
        loanProduct = loanProductOutputPort.save(loanProduct);
        List<Vendor> vendors = vendorOutputPort.saveVendors(List.of(vendorJuno));
        vendorJuno = vendors.get(0);
        vendorJunoId = vendorJuno.getId();
        loanProductId = loanProduct.getId();

    }

    @AfterAll
    void teardown() throws MeedlException {
        vendorOutputPort.deleteById(vendorJunoId);
        loanProductOutputPort.deleteById(loanProductId);
    }


    @Test
    @Order(1)
    void saveVendorsToLoanProduct() throws MeedlException {
        List<LoanProductVendor> savedLoanProductVendorEntity = loanProductVendorAdapter.save(List.of(vendorJuno), loanProduct);
        junoLoanProductVendorId = savedLoanProductVendorEntity.get(0).getId();
        assertNotNull(savedLoanProductVendorEntity);
        assertEquals(1, savedLoanProductVendorEntity.size());
        assertTrue(savedLoanProductVendorEntity.stream().allMatch(v -> v.getLoanProduct().getId().equals(loanProduct.getId())));
    }

    @Test
    @Order(2)
    void getVendorsByLoanProductId() throws MeedlException {

        List<Vendor> vendors = loanProductVendorAdapter.getVendorsByLoanProductId(loanProduct.getId());

        assertNotNull(vendors);
        assertEquals(1, vendors.size());
    }
    @Test
    @Order(3)
    void deleteLoanProductVendorById(){

        assertDoesNotThrow(() ->
                loanProductVendorAdapter.deleteById(junoLoanProductVendorId)
        );

        assertFalse(loanProductVendorRepository.findById(junoLoanProductVendorId).isPresent());
    }

    @Test
    void saveLoanProductVendorWithEmptyVendor() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                loanProductVendorAdapter.save(List.of(), loanProduct)
        );
        assertTrue(ex.getMessage().contains("Vendors to save to loan product cannot be empty"));
    }

    @Test
    void saveLoanProductVendorWithNullLoanProduct() {
        assertThrows(MeedlException.class, () ->
                loanProductVendorAdapter.save(List.of(vendorJuno), null)
        );
    }

    @Test
    void saveLoanProductVendorWithInvalidLoanProductId() {
        LoanProduct invalidLoanProduct = TestData.buildTestLoanProduct();
        invalidLoanProduct.setId("Invalid");
        MeedlException ex = assertThrows(MeedlException.class, () ->
                loanProductVendorAdapter.save(List.of(vendorJuno), invalidLoanProduct)
        );
        assertTrue(ex.getMessage().contains("Loan product id is required"));
    }

    @Test
    void deleteLoanProductVendorByInvalidId() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                loanProductVendorAdapter.deleteById("invalid-id")
        );
        assertTrue(ex.getMessage().contains("Invalid loan product vendor id"));
    }

    @Test
    void getVendorsByInvalidLoanProductId() {
        assertThrows(MeedlException.class, () ->
                loanProductVendorAdapter.getVendorsByLoanProductId("not-a-uuid")
        );
    }
}
