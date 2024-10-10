package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
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
    @Autowired
    private InvestmentVehicleEntityRepository investmentVehicleEntityRepository;
    private String investmentVehicleId;

    @BeforeEach
    void setUp() {
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

    }

    @Order(1)
    @Test
    void createInvestmentVehicle() throws MiddlException {
        assertThrows(InvestmentException.class, () -> investmentVehicleOutputPort.findById(capitalGrowth.getId()));
        InvestmentVehicle savedInvestmentVehicle =
                investmentVehicleOutputPort.save(capitalGrowth);
        assertNotNull(savedInvestmentVehicle);
        log.info("this is ======={}", savedInvestmentVehicle);
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(savedInvestmentVehicle.getId());
        investmentVehicleId = foundInvestmentVehicle.getId();
        assertEquals(foundInvestmentVehicle.getName(), savedInvestmentVehicle.getName());
    }


    @Test
    void createInvestmentVehicleWithNullInvestmentVehicleIdentity() {
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(null));
    }


    @Test
    void createInvestmentVehicleWithNullName() {
        capitalGrowth.setName(null);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyName() {
        capitalGrowth.setName(StringUtils.EMPTY);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullSponsors() {
        capitalGrowth.setSponsors(null);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptySponsors() {
        capitalGrowth.setSponsors(StringUtils.EMPTY);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithNullTenure() {
        capitalGrowth.setTenure(null);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyTenure() {
        capitalGrowth.setTenure(StringUtils.EMPTY);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullRate() {
        capitalGrowth.setRate(null);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullSize() {
        capitalGrowth.setSize(null);
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }
    @Order(2)
    @Test
    void UpdateInvestmentVehicleToCommercial() throws MiddlException {
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
    void updateInvestmentVehicleName() throws MiddlException {
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
    void updateInvestmentVehicleNameWithExistingInvestmentVehicleNameButTheSameEntity() throws MiddlException {
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
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.findById(fundGrowth.getId()));
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(fundGrowth));
    }

    @AfterAll
    void deleteAll() {
        investmentVehicleEntityRepository.deleteAll();
    }


}
