package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CouponDistributionOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CouponDistributionAdapterTest {


    private CouponDistribution couponDistribution;
    @Autowired
    private CouponDistributionOutputPort couponDistributionOutputPort;
    private String couponId;


    @BeforeAll
    void setUp() {
        couponDistribution = TestData.createCouponDistribution();
    }

    @Test
    void saveCouponDistribution() throws MeedlException {
        CouponDistribution savedCouponDistribution =
                couponDistributionOutputPort.save(couponDistribution);
        couponId = savedCouponDistribution.getId();
        assertNotNull(savedCouponDistribution);
        assertNotNull(savedCouponDistribution.getId());
    }


    @AfterAll
    void tearDown() throws MeedlException {
        couponDistributionOutputPort.deleteById(couponId);
    }
}
