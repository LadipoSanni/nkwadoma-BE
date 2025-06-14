package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CapitalDistributionOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.VehicleClosureOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CapitalDistribution;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.VehicleClosure;
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
public class VehicleClosureAdapterTest {

    private VehicleClosure vehicleClosure;
    private CapitalDistribution capitalDistribution;
    @Autowired
    private CapitalDistributionOutputPort capitalDistributionOutputPort;
    @Autowired
    private VehicleClosureOutputPort vehicleClosureOutputPort;
    private String vehicleId;

    @BeforeAll
    void setUp() {
        capitalDistribution = TestData.buildCapitalDistribution();
        try {
            capitalDistribution = capitalDistributionOutputPort.save(capitalDistribution);
            vehicleClosure = TestData.buildVehicleClosure(capitalDistribution);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
    }

    @Order(1)
    @Test
    void saveVehicleClosure() {
        VehicleClosure savedVehicleClosure = VehicleClosure.builder().build();
        try{
            savedVehicleClosure = vehicleClosureOutputPort.save(vehicleClosure);
            vehicleId = savedVehicleClosure.getId();
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(savedVehicleClosure);
    }

    @Test
    void cannotSaveNullVehicleClosure() {
        assertThrows(MeedlException.class, () -> vehicleClosureOutputPort.save(null));
    }

    @AfterAll
    void tearDown() throws MeedlException {
        vehicleClosureOutputPort.deleteById(vehicleId);
        capitalDistributionOutputPort.deleteById(capitalDistribution.getId());
    }
}
