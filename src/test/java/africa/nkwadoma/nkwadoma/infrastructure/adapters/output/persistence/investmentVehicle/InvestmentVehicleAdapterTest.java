package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

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
    private InvestmentVehicle investment;
    private String investmentVehicleId;
    private String investmentId;
    @Autowired
    private InvestmentVehicleEntityRepository investmentVehicleEntityRepository;



    @BeforeEach
    void setUp(){
        capitalGrowth = new InvestmentVehicle();
        capitalGrowth.setName("Growth Investment");
        capitalGrowth.setSize(BigDecimal.valueOf(4000));
        capitalGrowth.setRate(13F);
        capitalGrowth.setMandate("Long-term Growth");
        capitalGrowth.setSponsors("UBA");
        capitalGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        capitalGrowth.setTenure("12 Month");


        fundGrowth = new InvestmentVehicle();
        fundGrowth.setName("Growth Investment2");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setSponsors("UBA");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        fundGrowth.setTenure("12 Month");


        investment = new InvestmentVehicle();
        investment.setName("Investment");
        investment.setSize(BigDecimal.valueOf(4000));
        investment.setRate(12F);
        investment.setMandate("Long-term fund");
        investment.setSponsors("UBA");
        investment.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        investment.setTenure("12 Month");

    }

    @Order(1)
    @Test
    void createInvestmentVehicle() throws  MeedlException {
        assertThrows(InvestmentException.class,()-> investmentVehicleOutputPort.findById(capitalGrowth.getId()));
        InvestmentVehicle savedInvestmentVehicle =
                investmentVehicleOutputPort.save(capitalGrowth);
        assertNotNull(savedInvestmentVehicle);
        log.info("this is ======={}",savedInvestmentVehicle);
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(savedInvestmentVehicle.getId());
        investmentVehicleId = foundInvestmentVehicle.getId();
        assertEquals(foundInvestmentVehicle.getName(), savedInvestmentVehicle.getName());
    }


    @Test
    void checkIfInvestmentVehicleNameExistTest(){
        try {
            investmentVehicleOutputPort.checkIfInvestmentVehicleNameExist(capitalGrowth);
        } catch (MeedlException e) {
            assertEquals("Investment vehicle name exist",e.getMessage());
        }

    }

    @Test
    void checkIfInvestmentVehicleExistWithSameEntity(){
        try{
            InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.save(investment);
            investmentId = investmentVehicle.getId();
            investmentVehicleOutputPort.checkIfInvestmentVehicleNameExist(investmentVehicle);
        } catch (MeedlException exception) {
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void updateInvestmentVehicleRate() {
        try {
            InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            investmentVehicle.setRate(15F);
            investmentVehicleOutputPort.save(investmentVehicle);
        } catch (MeedlException exception) {
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
    void createInvestmentVehicleWithNullSponsors(){
        capitalGrowth.setSponsors(null);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptySponsors(){
        capitalGrowth.setSponsors(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithNullTenure() {
        capitalGrowth.setTenure(null);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyTenure() {
        capitalGrowth.setTenure(StringUtils.EMPTY);
        assertThrows(MeedlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullRate() {
        capitalGrowth.setRate(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullSize() {
        capitalGrowth.setSize(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Order(2)
    @Test
    void UpdateInvestmentVehicleToCommercial() throws MeedlException {
        InvestmentVehicle existingInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(investmentVehicleId);
        assertEquals(capitalGrowth.getInvestmentVehicleType().toString(), existingInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
        log.info("passed---------");
        existingInvestmentVehicleIdentity.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        InvestmentVehicle updatedInvestmentVehicleIdentity =
                investmentVehicleOutputPort.save(existingInvestmentVehicleIdentity);
        InvestmentVehicle foundUpdatedInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
        assertNotEquals(capitalGrowth.getInvestmentVehicleType().toString(), foundUpdatedInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
    }

    @Order(3)
    @Test
    void updateInvestmentVehicleName() throws MeedlException {
        InvestmentVehicle existingInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
        assertEquals(capitalGrowth.getName(),existingInvestmentVehicle.getName());
        log.info("passed---------");
        existingInvestmentVehicle.setName("Growth Investment2");
        InvestmentVehicle updatedInvestmentVehicleIdentity =
                investmentVehicleOutputPort.save(existingInvestmentVehicle);
        InvestmentVehicle foundUpdatedInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
        assertNotEquals(capitalGrowth.getName(),foundUpdatedInvestmentVehicleIdentity.getName());
    }

    @Order(4)
    @Test
    void updateInvestmentVehicleNameWithExistingInvestmentVehicleNameButTheSameEntity() throws MeedlException {
            InvestmentVehicle existingInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            assertNotNull(existingInvestmentVehicle);
            assertNotEquals(capitalGrowth.getName(),existingInvestmentVehicle.getName());
            existingInvestmentVehicle.setName("Growth Investment2");
            InvestmentVehicle updatedInvestmentVehicleIdentity = investmentVehicleOutputPort.save(existingInvestmentVehicle);
            assertEquals(existingInvestmentVehicle.getName(),updatedInvestmentVehicleIdentity.getName());
    }

    @Order(5)
    @Test
    void  createInvestmentVehicleWithExistingInvestmentVehicleName() {
    try {
        investmentVehicleOutputPort.save(fundGrowth);
    } catch (MeedlException e) {
        assertEquals("Investment vehicle name exist", e.getMessage());
    }
}

    @Order(6)
    @Test
    void updateNonExistentInvestmentVehicle() {
        try {
            InvestmentVehicle nonExistent = investmentVehicleOutputPort.findById("fake-id");
            nonExistent.setRate(20F);
            investmentVehicleOutputPort.save(nonExistent);
        }catch (MeedlException e){
            assertEquals("Investment vehicle not found",e.getMessage());
        }

    }

    @Test
    void findInvestmentVehicleDetailsWithNullId()  {
        try {
            investmentVehicleOutputPort.findById(null);
        }catch (MeedlException e){
            assertEquals("Investment vehicle id cannot be null",e.getMessage());
        }
    }


    @Test
    void findInvestmentVehicleDetailsWithFakeID() {
        try {
            investmentVehicleOutputPort.findById("Fake-id");
        }catch (MeedlException e){
            assertEquals("Investment vehicle not found",e.getMessage());
        }
    }

    @Order(7)
    @Test
    void findInvestmentVehicleDetailsById() throws MeedlException {

        InvestmentVehicle investmentVehicle =
                investmentVehicleOutputPort.findById(investmentVehicleId);
        assertNotNull(investmentVehicle);
        assertEquals(investmentVehicle.getId(),investmentVehicleId);

    }

    @AfterAll
    void cleanUp(){
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentId);
    }

}
