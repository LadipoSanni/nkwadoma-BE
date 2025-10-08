package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

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
    void viewAllVendors() throws MeedlException {
        Page<Vendor> vendorPage = new PageImpl<>(List.of(sampleVendor), PageRequest.of(0, 10), 1);
        when(vendorOutputPort.viewAllVendor(sampleVendor)).thenReturn(vendorPage);

        Page<Vendor> result = vendorService.viewAllVendors(sampleVendor);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getVendorName()).isEqualTo("Test Vendor");

        verify(vendorOutputPort, times(1)).viewAllVendor(sampleVendor);
    }

    @Test
    void viewAllProviderServices() throws MeedlException {
        Page<String> providerServicePage = new PageImpl<>(List.of("Provider service"), PageRequest.of(0, 10), 1);
        when(vendorOutputPort.viewAllProviderService(sampleVendor)).thenReturn(providerServicePage);

        Page<String> result = vendorService.viewAllProviderService(sampleVendor);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).equals("Test Vendor"));

        verify(vendorOutputPort, times(1)).viewAllProviderService(sampleVendor);
    }

    @Test
    void viewAllVendorsWithNullVendor() {
        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(null));

        assertThat(ex.getMessage()).contains("Vendor cannot be empty");
        verifyNoInteractions(vendorOutputPort);
    }

    @Test
    void testViewAllVendorsWithInvalidPageSize() {
        sampleVendor.setPageSize(0);

        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(sampleVendor));

        assertThat(ex.getMessage()).contains("Page size");
        verifyNoInteractions(vendorOutputPort);
    }

    @Test
    void testViewAllVendorsWithInvalidPageNumber() {
        sampleVendor.setPageNumber(-1); // invalid page number

        MeedlException ex = assertThrows(MeedlException.class,
                () -> vendorService.viewAllVendors(sampleVendor));

        assertThat(ex.getMessage()).contains("Page number");
        verifyNoInteractions(vendorOutputPort);
    }
}
