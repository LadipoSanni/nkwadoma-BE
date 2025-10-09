package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct;

import static org.junit.jupiter.api.Assertions.*;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorAdapterIntegrationTest {

    @Autowired
    private VendorAdapter vendorAdapter;

    @Autowired
    private VendorEntityRepository vendorEntityRepository;

    private Vendor vendor;
    private String vendorName;
    private String vendorId;
    private String providerServiceName;

    @BeforeAll
    void setup() {
        vendorName = TestUtils.generateName(9);
        providerServiceName = TestUtils.generateName(9);
        Set<String> providerServices = new HashSet<>();
        providerServices.add(providerServiceName);
        vendor = TestData.createTestVendor(vendorName, providerServices);

    }

    @Test
    @Order(1)
    void saveVendors() throws MeedlException {
        List<Vendor> result = vendorAdapter.saveVendors(List.of(vendor));
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getId());
        vendorId = result.get(0).getId();
    }

    @Test
    void saveVendorsWithEmptyList() {
        assertThrows(MeedlException.class, () -> vendorAdapter.saveVendors(List.of()));
    }

    @Test
    @Order(2)
    void viewAllVendorByName() throws MeedlException {
        vendor.setVendorName(vendorName);
        log.info("vendor name {}", vendorName);
        Page<Vendor> result = vendorAdapter.viewAllVendor(vendor);
        assertEquals(1, result.getTotalElements());
        assertEquals(vendorName, result.getContent().get(0).getVendorName());
    }

    @Test
    @Order(3)
    void viewAllVendor() throws MeedlException {

        vendor.setVendorName(null);
        Page<Vendor> result = vendorAdapter.viewAllVendor(vendor);

        assertEquals(1, result.getTotalElements());
    }



    @Test
    void viewAllVendorWithNullVendor() {
        assertThrows(MeedlException.class, () -> vendorAdapter.viewAllVendor(null));
    }


    @Test
    @Order(4)
    void viewAllProviderServiceByName() throws MeedlException {
        vendor.setVendorName(providerServiceName);
        Page<String> services = vendorAdapter.viewAllProviderService(vendor);
        assertEquals(1, services.getTotalElements());
    }

    @Test
    @Order(5)
    void viewAllProviderService() throws MeedlException {

        vendor.setVendorName(null);
        Page<String> services = vendorAdapter.viewAllProviderService(vendor);
        assertEquals(1, services.getTotalElements());
    }

    @Test
    void viewAllProviderServiceWithBlankName() throws MeedlException {
        vendor.setVendorName("");
        Page<String> services = vendorAdapter.viewAllProviderService(vendor);
        assertEquals(0, services.getTotalElements());
    }

    @Test
    void deleteVendorByIdWithInvalidUuid() {
        assertThrows(MeedlException.class, () -> vendorAdapter.deleteById("bad-uuid"));
    }

    @Test
    void deleteMultipleByIdEmptyList() {
        assertThrows(MeedlException.class, () -> vendorAdapter.deleteMultipleById(List.of()));
    }
    @Test
    @Order(6)
    void viewAllVendorWithInvalidPageSize() {
        vendor.setPageSize(-1);
        assertThrows(MeedlException.class, () -> vendorAdapter.viewAllVendor(vendor));
    }
    @Test
    @Order(7)
    void viewAllVendorWithBlankName() throws MeedlException {
        vendor.setVendorName("   ");
        vendor.setPageSize(10);
        Page<Vendor> result = vendorAdapter.viewAllVendor(vendor);
        assertEquals(0, result.getTotalElements());
    }
    @Test
    @Order(8)
    void deleteMultipleVendorById() throws MeedlException {
        VendorEntity e1 = new VendorEntity();
        e1.setVendorName("Multi1");
        e1.setCreatedAt(LocalDateTime.now());

        VendorEntity e2 = new VendorEntity();
        e2.setVendorName("Multi2");
        e2.setCreatedAt(LocalDateTime.now());

        List<VendorEntity> saved = vendorEntityRepository.saveAll(List.of(e1, e2));

        vendorAdapter.deleteMultipleById(saved.stream().map(VendorEntity::getId).toList());
        assertEquals(1, vendorEntityRepository.count());
    }
    @Test
    @Order(9)
    void deleteVendorById() throws MeedlException {

        vendorAdapter.deleteById(vendorId);
        assertFalse(vendorEntityRepository.findById(vendorId).isPresent());
    }
}
