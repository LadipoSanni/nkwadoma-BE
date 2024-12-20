package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

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
    private int pageSize = 1;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment limited");
    }

    @Order(1)
    @Test
    void createInvestmentVehicle() {
       try {
           InvestmentVehicle createdInvestmentVehicle =
                   investmentVehicleUseCase.createInvestmentVehicle(fundGrowth);
           investmentId = createdInvestmentVehicle.getId();
           assertNotNull(createdInvestmentVehicle);
       }catch (MeedlException exception){
           log.info("{} {}",exception.getClass().getName(), exception.getMessage());
       }
    }



    @Order(2)
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


    @Order(3)
    @Test
    void viewAllInvestmentVehicle(){
        Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase.viewAllInvestmentVehicle(
                pageSize, pageNumber);
        List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
        assertEquals(1, investmentVehiclesList.size());
    }


    @AfterAll
    void cleanUp() {
        try {
            investmentVehicleUseCase.deleteInvestmentVehicle(investmentId);
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

}
