package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

import static org.junit.jupiter.api.Assertions.*;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    private VendorOutputPort vendorOutputPort;

    @InjectMocks
    private VendorService vendorService;

    private Vendor sampleVendor;

    @BeforeEach
    void setUp() {
        sampleVendor = new Vendor();
        sampleVendor.setId("123");
        sampleVendor.setVendorName("Test Vendor");
        sampleVendor.setCostOfService(BigDecimal.valueOf(5000));
        sampleVendor.setPageNumber(0);
        sampleVendor.setPageSize(10);
    }

    @Test
    void testViewAllVendors_Success() throws MeedlException {
        // given
        Page<Vendor> vendorPage = new PageImpl<>(List.of(sampleVendor), PageRequest.of(0, 10), 1);
        when(vendorOutputPort.viewAllVendor(sampleVendor)).thenReturn(vendorPage);

        // when
        Page<Vendor> result = vendorService.viewAllVendors(sampleVendor);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getVendorName()).isEqualTo("Test Vendor");

        verify(vendorOutputPort, times(1)).viewAllVendor(sampleVendor);
    }

    @Test
    void testViewAllVendors_NullVendor_ThrowsException() {
        // when / then
        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(null));

        assertThat(ex.getMessage()).contains("Vendor cannot be empty");
        verifyNoInteractions(vendorOutputPort);
    }

    @Test
    void testViewAllVendors_InvalidPageSize_ThrowsException() {
        sampleVendor.setPageSize(0); // invalid page size

        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(sampleVendor));

        assertThat(ex.getMessage()).contains("Page size");
        verifyNoInteractions(vendorOutputPort);
    }

    @Test
    void testViewAllVendors_InvalidPageNumber_ThrowsException() {
        sampleVendor.setPageNumber(-1); // invalid page number

        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(sampleVendor));

        assertThat(ex.getMessage()).contains("Page number");
        verifyNoInteractions(vendorOutputPort);
    }
}
