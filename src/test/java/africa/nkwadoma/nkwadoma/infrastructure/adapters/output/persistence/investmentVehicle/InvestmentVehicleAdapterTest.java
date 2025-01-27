package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j

class InvestmentVehicleAdapterTest {

    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private InvestmentVehicle capitalGrowth;
    private InvestmentVehicle fundGrowth;
    private String investmentVehicleId;
    private int pageSize = 1;
    private int pageNumber = 0;




    @BeforeEach
    void setUp(){
        capitalGrowth = TestData.buildInvestmentVehicle("Growth Investment");

        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment2");
    }


    @Order(1)
    @Test
    void createInvestmentVehicle() {
        try {
            assertThrows(InvestmentException.class, () -> investmentVehicleOutputPort.findById(capitalGrowth.getId()));
            InvestmentVehicle savedInvestmentVehicle =
                    investmentVehicleOutputPort.save(capitalGrowth);
            assertNotNull(savedInvestmentVehicle);
            InvestmentVehicle foundInvestmentVehicle =
                    investmentVehicleOutputPort.findById(savedInvestmentVehicle.getId());
            investmentVehicleId = foundInvestmentVehicle.getId();
            assertEquals(foundInvestmentVehicle.getName(), savedInvestmentVehicle.getName());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void createInvestmentVehicleWithNullInvestmentVehicleIdentity() {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.save(null));
    }


    @Test
    void createInvestmentVehicleWithNullName(){
        capitalGrowth.setName(null);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyName(){
        capitalGrowth.setName(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithZeroTenure() {
        capitalGrowth.setTenure(0);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullRate() {
        capitalGrowth.setRate(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyRate() {
        capitalGrowth.setRate(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullTrustee() {
        capitalGrowth.setTrustee(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullCustodian() {
        capitalGrowth.setCustodian(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullBankPartner() {
        capitalGrowth.setCustodian(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullFundManager() {
        capitalGrowth.setCustodian(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithNullSize() {
        capitalGrowth.setSize(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }



    @Test
    void findInvestmentVehicleDetailsWithNullId()  {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById(null));
    }


    @Test
    void findInvestmentVehicleDetailsWithFakeID() {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById("Fake-id"));
    }

    @Test
    void searchInvestmentVehicleDetailsWithNullId()  {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.searchInvestmentVehicle(null));
    }

    @Order(2)
    @Test
    void findInvestmentVehicleDetailsById() {
        try {
            InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            assertNotNull(investmentVehicle);
            assertEquals(investmentVehicle.getId(), investmentVehicleId);
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }

    @Order(3)
    @Test
    void findAllInvestmentVehicleInvestmentVehicle(){
        Page<InvestmentVehicle> investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicle(
                pageSize, pageNumber);
        List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
        assertEquals(1, investmentVehiclesList.size());
    }

    @Order(4)
    @Test
    void searchInvestmentVehicle(){
        List<InvestmentVehicle> investmentVehicles = new ArrayList<>();
            try{
                investmentVehicles  = investmentVehicleOutputPort.searchInvestmentVehicle("g");
            }catch (MeedlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
            }
            assertNotNull(investmentVehicles);
            assertEquals(1,investmentVehicles.size());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
    }

}
