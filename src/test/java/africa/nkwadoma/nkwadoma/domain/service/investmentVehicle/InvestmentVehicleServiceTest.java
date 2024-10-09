package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
public class InvestmentVehicleServiceTest {


    @Autowired
    private CreateInvestmentVehicleUseCase investmentVehicleUseCase;
    private InvestmentVehicleIdentity fundGrowth;

    @Autowired
    private InvestmentVehicleIdentityOutputPort outputPort;

    @BeforeEach
    void setUp(){
        fundGrowth = new InvestmentVehicleIdentity();
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
    void createInvestmentVehicle() throws MiddlException {
       InvestmentVehicleIdentity createdInvestmentVehicle =
               investmentVehicleUseCase.createInvestmentVehicle(fundGrowth);
       assertNotNull(createdInvestmentVehicle);
    }

}
