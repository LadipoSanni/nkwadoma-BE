package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CouponDistributionOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.VehicleOperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.VehicleOperation;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class VehicleOperationAdapterTest {

    private VehicleOperation vehicleOperation;
    private CouponDistribution couponDistribution;
    @Autowired
    private CouponDistributionOutputPort couponDistributionOutputPort;
    @Autowired
    private VehicleOperationOutputPort vehicleOperationOutputPort;
    private String vehicleOperationId;

    @BeforeAll
    void setUp() throws MeedlException {
        couponDistribution = TestData.createCouponDistribution();
        couponDistribution = couponDistributionOutputPort.save(couponDistribution);
        vehicleOperation = TestData.createVehicleOperation(couponDistribution);
    }

    @Order(1)
    @Test
    void saveVehicleOperation() {
        VehicleOperation savedVehicleOperation = VehicleOperation.builder().build();
        try {
            savedVehicleOperation = vehicleOperationOutputPort.save(vehicleOperation);
            vehicleOperationId = savedVehicleOperation.getId();
        }catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
        assertNotNull(savedVehicleOperation.getId());
        assertNotNull(savedVehicleOperation);
    }

    @Test
    void cannotSaveVehicleOperationWithNullCouponDistribution() {
        vehicleOperation.setCouponDistribution(null);
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(vehicleOperation));
    }

    @Test
    void cannotSaveVehicleOperationWithNullVehicleOperation() {
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(null));
    }

    @Test
    void cannotSaveVehicleOperationWithNullCouponDistributionStatus() {
        vehicleOperation.setCouponDistributionStatus(null);
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(vehicleOperation));
    }

    @Test
    void cannotSaveVehicleOperationWithNullOperationStatus() {
        vehicleOperation.setOperationStatus(null);
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(vehicleOperation));
    }

    @Test
    void cannotSaveVehicleOperationWithNullFundRaisingStatus() {
        vehicleOperation.setFundRaisingStatus(null);
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(vehicleOperation));
    }

    @Test
    void cannotSaveVehicleOperationWithNullDeployingStatus() {
        vehicleOperation.setDeployingStatus(null);
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(vehicleOperation));
    }

    @AfterAll
    void tearDown() throws MeedlException {
        vehicleOperationOutputPort.deleteById(vehicleOperationId);
        couponDistributionOutputPort.deleteById(couponDistribution.getId());
    }
}
