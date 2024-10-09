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

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class InvestmentVehicleIdentityAdapterTest {

    @Autowired
    private InvestmentVehicleIdentityOutputPort investmentVehicleIdentityOutputPort;
    private InvestmentVehicleIdentity capitalGrowth;
    private InvestmentVehicleIdentity fundGrowth;
    @Autowired
    private InvestmentVehicleEntityRepository investmentVehicleEntityRepository;



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
        fundGrowth.setName("Growth Investment");
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

    @Order(2)
    @Test
    void createInvestmentVehicleNameWithExistingInvestmentVehicleName() {
        assertThrows(MiddlException.class, () -> investmentVehicleIdentityOutputPort.findById(fundGrowth.getId()));
        assertThrows(MiddlException.class, () -> investmentVehicleIdentityOutputPort.save(fundGrowth));
    }

    @AfterAll
     void deleteAll(){
        investmentVehicleEntityRepository.deleteAll();
    }

}
