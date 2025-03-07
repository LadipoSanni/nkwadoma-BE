package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;
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
    private String draftInvestmentVehicleId;




    @BeforeEach
    void setUp(){
        capitalGrowth = TestData.buildInvestmentVehicle("Growth Investment");

        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment2");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
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

    @Test
    void cannotCreateEndowmentInvestmentVehicleWithNullRate(){
        capitalGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        capitalGrowth.setRate(null);
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void cannotSaveToDraftWithNullName(){
        capitalGrowth.setName(null);
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
    }

    @Test
    void cannotSaveToDraftWithEmptyName(){
        capitalGrowth.setName(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.save(capitalGrowth));
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


    @Order(5)
    @Test
    void investmentVehicleCanBeSavedToDraft(){
        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("Save To Draft");
        investmentVehicle.setInvestmentVehicleStatus(DRAFT);
        investmentVehicle.setSize(null);
        investmentVehicle.setRate(null);
        try{
            investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
            draftInvestmentVehicleId = investmentVehicle.getId();
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicle);
        assertEquals(DRAFT, investmentVehicle.getInvestmentVehicleStatus());
    }

    @Order(6)
    @Test
    void viewAllInvestmentVehicleByType() {
        try {
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
            assertNotNull(investmentVehiclesList);
            assertEquals(1, investmentVehiclesList.size());
            assertEquals(InvestmentVehicleType.ENDOWMENT, investmentVehiclesList.get(0).getInvestmentVehicleType());
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Order(7)
    @Test
    void viewAllInvestmentVehicleByTypeCommercial() {
        try {
            investmentVehicleOutputPort.save(fundGrowth);
            capitalGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.COMMERCIAL);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();

            assertNotNull(investmentVehiclesList);
            assertEquals(1, investmentVehiclesList.size());
            assertEquals(InvestmentVehicleType.COMMERCIAL, investmentVehiclesList.get(0).getInvestmentVehicleType());
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Order(8)
    @Test
    void viewAllInvestmentVehicleByTypeDoesNotReturnDraft() {
        try {
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
            assertNotNull(investmentVehiclesList);
            assertEquals(1, investmentVehiclesList.size());
            assertEquals(InvestmentVehicleStatus.PUBLISHED, investmentVehiclesList.get(0).getInvestmentVehicleStatus());
        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void viewAllInvestmentVehicleByTypeThrowExceptionForNullParameter() {
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber, null));
    }


    @Test
    void viewAllInvestmentVehiclePassingNullParameter(){
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber,null));
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(draftInvestmentVehicleId);
    }

}
