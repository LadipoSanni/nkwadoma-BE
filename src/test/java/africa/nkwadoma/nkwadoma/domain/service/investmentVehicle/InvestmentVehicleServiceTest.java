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
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.FundRaisingStatus.DEPLOYING;
import static africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType.ENDOWMENT;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class InvestmentVehicleServiceTest {


    @Autowired
    private CreateInvestmentVehicleUseCase investmentVehicleUseCase;
    private InvestmentVehicle fundGrowth;
    private String investmentId;

    @Autowired
    private InvestmentVehicleOutputPort outputPort;

    @BeforeEach
    void setUp(){
        fundGrowth = new InvestmentVehicle();
        fundGrowth.setName("Growth Investment limited");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setSponsors("UBA");
        fundGrowth.setInvestmentVehicleType(ENDOWMENT);
        fundGrowth.setTenure("12 Month");


    }

    @Test
    @Order(1)
    void createInvestmentVehicle() throws MeedlException {
       InvestmentVehicle createdInvestmentVehicle =
               investmentVehicleUseCase.createOrUpdateInvestmentVehicle(fundGrowth);
       investmentId = createdInvestmentVehicle.getId();
       assertNotNull(createdInvestmentVehicle);
    }


    @Test
    @Order(2)
    void updateInvestmentVehicleFundRaisingStatus() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setFundRaisingStatus(DEPLOYING);
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertEquals(updatedInvestmentVehicle.getFundRaisingStatus().toString(),
                DEPLOYING.toString());
    }

    @Test
    @Order(3)
    void updateInvestmentVehicleName() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setName("Growth Investment limited2");
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertNotEquals(fundGrowth.getName(), updatedInvestmentVehicle.getName());
    }

    @Test
    @Order(4)
    void updateInvestmentVehicleRate() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setRate(14F);
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertNotEquals(fundGrowth.getRate(),updatedInvestmentVehicle.getRate());
    }


    @Test
    @Order(5)
    void updateInvestmentVehicleType() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setInvestmentVehicleType(ENDOWMENT);
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertNotEquals(InvestmentVehicleType.COMMERCIAL.toString(),
                updatedInvestmentVehicle.getFundRaisingStatus().toString());
    }


    @Test
    @Order(5)
    void updateInvestmentVehicleSponsors() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setSponsors("Gt");
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertNotEquals(fundGrowth.getSponsors(),updatedInvestmentVehicle.getSponsors());
    }


    @Test
    @Order(5)
    void updateInvestmentVehicleMandate() throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                outputPort.findById(investmentId);
        foundInvestmentVehicle.setMandate("mandate");
        InvestmentVehicle updatedInvestmentVehicle =
                investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
        assertNotEquals(fundGrowth.getMandate(),updatedInvestmentVehicle.getMandate());
    }

    @Test
    @Order(6)
    void viewAllInvestmentVehicle(){
        List<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicle();
        assertEquals(1,investmentVehicles.size());
    }

    @AfterAll
    void cleanUp(){
        investmentVehicleUseCase.deleteInvestmentVehicle(investmentId);
    }

}
