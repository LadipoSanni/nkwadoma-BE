package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class InvestmentVehicleServiceTest {


    @Autowired
    private CreateInvestmentVehicleUseCase investmentVehicleUseCase;
    private InvestmentVehicle fundGrowth;

    @Autowired
    private InvestmentVehicleOutputPort outputPort;

    @BeforeEach
    void setUp() {
        fundGrowth = new InvestmentVehicle();
        fundGrowth.setName("Growth Investment limited");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setSponsors("UBA");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        fundGrowth.setTenure("12 Month");

    }

    @Test
    @Order(1)
    void createInvestmentVehicle() throws MeedlException {
        InvestmentVehicle createdInvestmentVehicle =
                investmentVehicleUseCase.createInvestmentVehicle(fundGrowth);
        assertNotNull(createdInvestmentVehicle);
    }

    @Test
    @Order(3)
    void viewInvestmentVehicleDetails() {
        try {
            InvestmentVehicle viewedInvestmentVehicle =
                    investmentVehicleUseCase.viewInvestmentVehicleDetails("");
            assertNotNull(viewedInvestmentVehicle);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }

}
