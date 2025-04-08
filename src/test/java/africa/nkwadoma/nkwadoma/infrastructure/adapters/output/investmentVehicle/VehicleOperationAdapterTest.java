package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CouponDistributionOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.VehicleOperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.OperationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.VehicleOperation;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

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
    void cannotSaveVehicleOperationWithNullVehicleOperation() {
        assertThrows(MeedlException.class, () -> vehicleOperationOutputPort.save(null));
    }

    @Order(2)
    @Test
    void changeVehicleOperationStatus() {
        VehicleOperation operation = VehicleOperation.builder().build();
        vehicleOperation.setId(vehicleOperationId);
        vehicleOperation.setOperationStatus(null);
        vehicleOperation.setDeployingStatus(InvestmentVehicleMode.CLOSE);
        vehicleOperation.setFundRaisingStatus(InvestmentVehicleMode.CLOSE);
        vehicleOperation.setCouponDistributionStatus(CouponDistributionStatus.PERFORMING);
        try{
            operation = vehicleOperationOutputPort.changeOperationStatuses(vehicleOperation);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertEquals(vehicleOperationId,operation.getId());
        assertEquals(InvestmentVehicleMode.CLOSE,operation.getFundRaisingStatus());
        assertEquals(InvestmentVehicleMode.CLOSE,operation.getDeployingStatus());
        assertEquals(CouponDistributionStatus.PERFORMING,operation.getCouponDistributionStatus());
        assertEquals(OperationStatus.ACTIVE,operation.getOperationStatus());
    }

    @Order(3)
    @Test
    void changeVehicleOperationStatusWithNullStatusesButTheOperationStatusDosentChangeToNull() {
        VehicleOperation operation = VehicleOperation.builder().build();
        vehicleOperation.setId(vehicleOperationId);
        vehicleOperation.setOperationStatus(null);
        vehicleOperation.setDeployingStatus(null);
        vehicleOperation.setFundRaisingStatus(null);
        vehicleOperation.setCouponDistributionStatus(null);
        try{
            operation = vehicleOperationOutputPort.changeOperationStatuses(vehicleOperation);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertEquals(vehicleOperationId,operation.getId());
        assertEquals(InvestmentVehicleMode.CLOSE,operation.getFundRaisingStatus());
        assertEquals(InvestmentVehicleMode.CLOSE,operation.getDeployingStatus());
        assertEquals(CouponDistributionStatus.PERFORMING,operation.getCouponDistributionStatus());
        assertEquals(OperationStatus.ACTIVE,operation.getOperationStatus());
    }

    @AfterAll
    void tearDown() throws MeedlException {
        vehicleOperationOutputPort.deleteById(vehicleOperationId);
        couponDistributionOutputPort.deleteById(couponDistribution.getId());
    }
}
