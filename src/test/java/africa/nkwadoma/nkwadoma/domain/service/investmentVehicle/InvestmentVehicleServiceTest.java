package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

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
    void setUpInvestmentVehicle() {
       try {
           InvestmentVehicle createdInvestmentVehicle =
                   investmentVehicleUseCase.setUpInvestmentVehicle(fundGrowth);
           investmentId = createdInvestmentVehicle.getId();
           assertNotNull(createdInvestmentVehicle);
       }catch (MeedlException exception){
           log.info("{} {}",exception.getClass().getName(), exception.getMessage());
       }
    }

    @Test
    void setUpInvestmentVehicleWithTenureGreaterThanThreeDigits() {
        fundGrowth.setTenure(9999);
        MeedlException meedlException =
                assertThrows(MeedlException.class, () -> investmentVehicleUseCase.setUpInvestmentVehicle(fundGrowth));
        log.info("Exception occurred: {} {}",meedlException.getClass().getName(), meedlException.getMessage());
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

    @Order(4)
    @Test
    void publishInvestmentVehicle() {
        InvestmentVehicle investmentVehicle = new InvestmentVehicle();
        try {
             investmentVehicle = investmentVehicleUseCase.publishInvestmentVehicle(investmentId);
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(InvestmentVehicleStatus.PUBLISHED,investmentVehicle.getInvestmentVehicleStatus());
        assertNotNull(investmentVehicle.getInvestmentVehicleLink());
    }

    @Order(5)
    @Test
    void viewAllInvestmentVehiclesByType(){
        try{
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase.viewAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
            assertEquals(1, investmentVehiclesList.size());
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
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
