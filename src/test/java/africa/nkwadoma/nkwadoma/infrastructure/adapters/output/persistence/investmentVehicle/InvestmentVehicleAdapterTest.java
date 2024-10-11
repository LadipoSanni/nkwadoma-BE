package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
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
        assertThrows(InvestmentException.class,()-> investmentVehicleOutputPort.findById(capitalGrowth.getId()));
        InvestmentVehicle savedInvestmentVehicle =
                investmentVehicleOutputPort.save(capitalGrowth);
        assertNotNull(savedInvestmentVehicle);
        log.info("this is ======={}",savedInvestmentVehicle);
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(savedInvestmentVehicle.getId());
        assertEquals(foundInvestmentVehicle.getName(),savedInvestmentVehicle.getName());
    }


    @Test
    void createInvestmentVehicleWithNullInvestmentVehicleIdentity(){
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(null));
    }


    @Test
    void createInvestmentVehicleWithNullName(){
        capitalGrowth.setName(null);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyName(){
        capitalGrowth.setName(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullSponsors(){
        capitalGrowth.setSponsors(null);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptySponsors(){
        capitalGrowth.setSponsors(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }


    @Test
    void createInvestmentVehicleWithNullTenure(){
        capitalGrowth.setTenure(null);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithEmptyTenure(){
        capitalGrowth.setTenure(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()-> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullRate(){
        capitalGrowth.setRate(null);
        assertThrows(MiddlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void createInvestmentVehicleWithNullSize(){
        capitalGrowth.setSize(null);
        assertThrows(MiddlException.class,()->investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Order(2)
    @Test
    void createInvestmentVehicleNameWithExistingInvestmentVehicleName() {
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.findById(fundGrowth.getId()));
        assertThrows(MiddlException.class, () -> investmentVehicleOutputPort.save(fundGrowth));
    }



    @AfterAll
     void deleteAll(){
        investmentVehicleEntityRepository.deleteAll();
    }

}
