package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus.DEPLOYING;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType.ENDOWMENT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class InvestmentVehicleServiceTest {


    @Autowired
    private CreateInvestmentVehicleUseCase investmentVehicleUseCase;
    private InvestmentVehicle fundGrowth;
    private String investmentId;

    @Autowired
    private InvestmentVehicleOutputPort outputPort;

    @BeforeEach
    void setUp() {
        fundGrowth = new InvestmentVehicle();
        fundGrowth.setName("Growth Investment limited");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setInvestmentVehicleType(ENDOWMENT);
        fundGrowth.setTenure(12);
    }

    @Test
    void createInvestmentVehicle() {
       try {
           InvestmentVehicle createdInvestmentVehicle =
                   investmentVehicleUseCase.createOrUpdateInvestmentVehicle(fundGrowth);
           investmentId = createdInvestmentVehicle.getId();
           assertNotNull(createdInvestmentVehicle);
       }catch (MeedlException exception){
           log.info("{} {}",exception.getClass().getName(), exception.getMessage());
       }
    }


    @Test
    void updateInvestmentVehicleFundRaisingStatus() {
        try {
            InvestmentVehicle foundInvestmentVehicle =
                    outputPort.findById(investmentId);
            foundInvestmentVehicle.setFundRaisingStatus(DEPLOYING);
            InvestmentVehicle updatedInvestmentVehicle =
                    investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
            assertEquals(updatedInvestmentVehicle.getFundRaisingStatus().toString(),
                    DEPLOYING.toString());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void updateInvestmentVehicleName() {
        try{
            InvestmentVehicle foundInvestmentVehicle =
                    outputPort.findById(investmentId);
            foundInvestmentVehicle.setName("Growth Investment limited2");
            InvestmentVehicle updatedInvestmentVehicle =
                    investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
            assertNotEquals(fundGrowth.getName(), updatedInvestmentVehicle.getName());
        } catch (MeedlException exception) {
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void updateInvestmentVehicleRate() {
        try{
            InvestmentVehicle foundInvestmentVehicle =
                    outputPort.findById(investmentId);
            foundInvestmentVehicle.setRate(14F);
            InvestmentVehicle updatedInvestmentVehicle =
                    investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
            assertNotEquals(fundGrowth.getRate(),updatedInvestmentVehicle.getRate());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    @Order(5)
    void updateInvestmentVehicleType()  {
        try{
            InvestmentVehicle foundInvestmentVehicle =
                    outputPort.findById(investmentId);
            foundInvestmentVehicle.setInvestmentVehicleType(ENDOWMENT);
            InvestmentVehicle updatedInvestmentVehicle =
                    investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
            assertNotEquals(InvestmentVehicleType.COMMERCIAL.toString(),
                    updatedInvestmentVehicle.getFundRaisingStatus().toString());
        } catch (MeedlException exception) {
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }

    }


    @Test
    void updateInvestmentVehicleMandate() {
        try {
            InvestmentVehicle foundInvestmentVehicle =
                    outputPort.findById(investmentId);
            foundInvestmentVehicle.setMandate("mandate");
            InvestmentVehicle updatedInvestmentVehicle =
                    investmentVehicleUseCase.createOrUpdateInvestmentVehicle(foundInvestmentVehicle);
            assertNotEquals(fundGrowth.getMandate(), updatedInvestmentVehicle.getMandate());
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewInvestmentVehicleDetails() {
        try {
            InvestmentVehicle viewedInvestmentVehicle = investmentVehicleUseCase.viewInvestmentVehicleDetails(investmentId);
            assertNotNull(viewedInvestmentVehicle);
            assertEquals(viewedInvestmentVehicle.getId(),investmentId);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }

    @Test
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
