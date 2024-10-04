package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Slf4j
class InvestmentVehicleIdentityAdapterTest {

    @Autowired
    private InvestmentVehicleIdentityOutputPort investmentVehicleIdentityOutputPort;
    private InvestmentVehicleEntityRepository investmentVehicleEntityRepository;
    private InvestmentVehicleIdentity capitalGrowth;
    private InvestmentVehicleIdentity fundGrowth;
    private String investmentVehicleId;



    @BeforeEach
    void setUp(){
        capitalGrowth = new InvestmentVehicleIdentity();
        capitalGrowth.setName("Growth Investment");
        capitalGrowth.setSize(BigDecimal.valueOf(4000));
        capitalGrowth.setRate(13F);
        capitalGrowth.setMandate("Long-term Growth");
        capitalGrowth.setSponsors("UBA");
        capitalGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        capitalGrowth.setTenure("12 Month");


        fundGrowth = new InvestmentVehicleIdentity();
        fundGrowth.setName("Growth Investment2");
        fundGrowth.setSize(BigDecimal.valueOf(4000));
        fundGrowth.setRate(12F);
        fundGrowth.setMandate("Long-term fund");
        fundGrowth.setSponsors("UBA");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        fundGrowth.setTenure("12 Month");
        fundGrowth.setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);


    }

    @Order(1)
    @Test
    void createInvestmentVehicle() throws MiddlException {
        assertThrows(InvestmentException.class,()->investmentVehicleIdentityOutputPort.findById(capitalGrowth.getId()));
        InvestmentVehicleIdentity savedInvestmentVehicle =
                investmentVehicleIdentityOutputPort.save(capitalGrowth);
        assertNotNull(savedInvestmentVehicle);
        log.info("this is ======={}",savedInvestmentVehicle);
        InvestmentVehicleIdentity foundInvestmentVehicle =
                investmentVehicleIdentityOutputPort.findById(savedInvestmentVehicle.getId());
       investmentVehicleId = savedInvestmentVehicle.getId();
        assertEquals(foundInvestmentVehicle.getName(),savedInvestmentVehicle.getName());
    }


    @Test
    void createInvestmentVehicleWithNullInvestmentVehicleIdentity(){
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(null));
    }


    @Test
    void createInvestmentVehicleWithNullName(){
        capitalGrowth.setName(null);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyName(){
        capitalGrowth.setName(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }

    @Order(2)
    @Test
    void UpdateInvestmentVehicleToCommercial() throws MiddlException {
        InvestmentVehicleIdentity existingInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.findById(investmentVehicleId);
        assertEquals(capitalGrowth.getInvestmentVehicleType().toString(),existingInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
        log.info("passed---------");
        existingInvestmentVehicleIdentity.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        InvestmentVehicleIdentity updatedInvestmentVehicleIdentity =
                investmentVehicleIdentityOutputPort.save(existingInvestmentVehicleIdentity);
        InvestmentVehicleIdentity foundUpdatedInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
        assertNotEquals(capitalGrowth.getInvestmentVehicleType().toString(),foundUpdatedInvestmentVehicleIdentity.getInvestmentVehicleType().toString());
    }


    @Test
    void createInvestmentVehicleWithNullSponsors(){
        capitalGrowth.setSponsors(null);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithEmptySponsors(){
        capitalGrowth.setSponsors(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithNullTenure(){
        capitalGrowth.setTenure(null);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyTenure(){
        capitalGrowth.setTenure(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(capitalGrowth));
    }

    @Order(3)
    @Test
    void updateInvestmentVehicle() throws MiddlException {
        InvestmentVehicleIdentity existingInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.findById(investmentVehicleId);
        assertEquals(capitalGrowth.getName(),existingInvestmentVehicleIdentity.getName());
        log.info("passed---------");
        existingInvestmentVehicleIdentity.setName("Growth Investment2");
        InvestmentVehicleIdentity updatedInvestmentVehicleIdentity =
                investmentVehicleIdentityOutputPort.save(existingInvestmentVehicleIdentity);
        InvestmentVehicleIdentity foundUpdatedInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.findById(updatedInvestmentVehicleIdentity.getId());
        assertNotEquals(capitalGrowth.getName(),foundUpdatedInvestmentVehicleIdentity.getName());
    }

    @Order(4)
    @Test
    void updateInvestmentVehicleNameWithExistingInvestmentVehicleNameButTheSameEntity() throws MiddlException {
        InvestmentVehicleIdentity existingInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.findById(investmentVehicleId);
        assertNotNull(existingInvestmentVehicleIdentity);
        assertNotEquals(capitalGrowth.getName(),existingInvestmentVehicleIdentity.getName());
        existingInvestmentVehicleIdentity.setName("Growth Investment2");
        InvestmentVehicleIdentity updatedInvestmentVehicleIdentity = investmentVehicleIdentityOutputPort.save(existingInvestmentVehicleIdentity);
        assertEquals(existingInvestmentVehicleIdentity.getName(),updatedInvestmentVehicleIdentity.getName());
    }

    @Order(5)
    @Test
    void createInvestmentVehicleNameWithExistingInvestmentVehicleName() {
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.findById(fundGrowth.getId()));
        assertThrows(MiddlException.class,()->investmentVehicleIdentityOutputPort.save(fundGrowth));
    }

    @AfterAll
    void cleanUp(){
        investmentVehicleEntityRepository.deleteAll();
    }

}
