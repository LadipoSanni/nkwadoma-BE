package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
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
        capitalGrowth = new InvestmentVehicle();
        capitalGrowth.setName("Growth Investment");
        capitalGrowth.setSize(BigDecimal.valueOf(4000));
        capitalGrowth.setRate(13F);
        capitalGrowth.setMandate("Long-term Growth");
        capitalGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        capitalGrowth.setTenure(12);


        fundGrowth = new InvestmentVehicle();
        fundGrowth.setName("Growth Investment2");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        fundGrowth.setTenure(12);
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


    @Order(2)
    @Test
    void updateInvestmentVehicleRate() {
        try {
            InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            investmentVehicle.setRate(15F);
            InvestmentVehicle savedInvestmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
            assertEquals(investmentVehicle.getRate(),savedInvestmentVehicle.getRate());
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
    void createInvestmentVehicleWithNullSize() {
        capitalGrowth.setSize(null);
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }


    @Order(3)
    @Test
    void UpdateInvestmentVehicleToCommercial() {
        try {
            InvestmentVehicle existingInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(investmentVehicleId);
            assertEquals(capitalGrowth.getInvestmentVehicleType().toString(), existingInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
            existingInvestmentVehicleIdentity.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
            InvestmentVehicle updatedInvestmentVehicleIdentity =
                    investmentVehicleOutputPort.save(existingInvestmentVehicleIdentity);
            InvestmentVehicle foundUpdatedInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
            assertNotEquals(capitalGrowth.getInvestmentVehicleType().toString(), foundUpdatedInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(4)
    @Test
    void updateInvestmentVehicleName()  {
        try {
            InvestmentVehicle existingInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            assertEquals(capitalGrowth.getName(), existingInvestmentVehicle.getName());
            existingInvestmentVehicle.setName("Growth Investment2");
            InvestmentVehicle updatedInvestmentVehicleIdentity =
                    investmentVehicleOutputPort.save(existingInvestmentVehicle);
            InvestmentVehicle foundUpdatedInvestmentVehicleIdentity = investmentVehicleOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
            assertNotEquals(capitalGrowth.getName(), foundUpdatedInvestmentVehicleIdentity.getName());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(5)
    @Test
    void updateInvestmentVehicleNameWithExistingInvestmentVehicleNameButTheSameEntity() {
        try {
            InvestmentVehicle existingInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            assertNotNull(existingInvestmentVehicle);
            assertNotEquals(capitalGrowth.getName(),existingInvestmentVehicle.getName());
            existingInvestmentVehicle.setName("Growth Investment2");
            InvestmentVehicle updatedInvestmentVehicleIdentity = investmentVehicleOutputPort.save(existingInvestmentVehicle);
            assertEquals(existingInvestmentVehicle.getName(),updatedInvestmentVehicleIdentity.getName());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void  createInvestmentVehicleWithExistingInvestmentVehicleName() {
        assertThrows(MeedlException.class,()->investmentVehicleOutputPort.save(fundGrowth));
    }

    @Test
    void updateNonExistentInvestmentVehicle() {
        try {
            InvestmentVehicle nonExistent = investmentVehicleOutputPort.findById("fake-id");
            nonExistent.setRate(20F);
            assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.save(nonExistent));
        }catch (MeedlException e){
            log.info("{} {}",e.getClass().getName(), e.getMessage());
        }

    }

    @Test
    void findInvestmentVehicleDetailsWithNullId()  {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById(null));
    }


    @Test
    void findInvestmentVehicleDetailsWithFakeID() {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById("Fake-id"));
    }


    @Order(6)
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

    @Order(7)
    @Test
    void findAllInvestmentVehicleInvestmentVehicle(){
        Page<InvestmentVehicle> investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicle(
                pageSize, pageNumber);
        List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
        assertEquals(1, investmentVehiclesList.size());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
    }

}
