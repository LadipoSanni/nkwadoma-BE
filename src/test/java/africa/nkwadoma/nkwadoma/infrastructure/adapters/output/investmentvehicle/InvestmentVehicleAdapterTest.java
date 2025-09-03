package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
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
    private InvestmentVehicle seaGrowth;
    private String investmentVehicleId;
    private int pageSize = 1;
    private int pageNumber = 0;
    private String draftInvestmentVehicleId;
    private String seaGrowthInvestmentVehicleId;
    private String fundRaisingInvestmentId;
    private String commercialInvestmentId;
    private String seaGrowthDraftInvestmentId;

    @BeforeEach
    void setUp(){
        capitalGrowth = TestData.buildInvestmentVehicle("Capital Growth");
        capitalGrowth.setFundRaisingStatus(FundRaisingStatus.CLOSED);

        fundGrowth = TestData.buildInvestmentVehicle("Fund Growth");
        fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);

        seaGrowth = TestData.buildInvestmentVehicle("Sea Growth");
        seaGrowth.setInvestmentVehicleType(InvestmentVehicleType.COMMERCIAL);
        seaGrowth.setInvestmentVehicleStatus(InvestmentVehicleStatus.DRAFT);
    }


    @Order(1)
    @Test
    void createInvestmentVehicle() {
        try {
            assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById(capitalGrowth.getId()));
            InvestmentVehicle savedInvestmentVehicle =
                    investmentVehicleOutputPort.save(capitalGrowth);
            assertNotNull(savedInvestmentVehicle);
            InvestmentVehicle foundInvestmentVehicle =
                    investmentVehicleOutputPort.findById(savedInvestmentVehicle.getId());
            log.info("Investment vehicle details in view by id test : {}", foundInvestmentVehicle);
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
    void findInvestmentVehicleDetailsWithNullId()  {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById(null));
    }


    @Test
    void findInvestmentVehicleDetailsWithFakeID() {
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.findById("Fake-id"));
    }

    @Test
    void searchInvestmentVehicleDetailsWithNullInvestmentVehicleStatus()  {
        fundGrowth.setInvestmentVehicleStatus(null);
        assertThrows(MeedlException.class, () -> investmentVehicleOutputPort.
                searchInvestmentVehicle("g",fundGrowth,pageSize,pageNumber));
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
            Page<InvestmentVehicle> investmentVehicles = null;
            fundGrowth.setInvestmentVehicleStatus(PUBLISHED);
            fundGrowth.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
            try{
                investmentVehicles  = investmentVehicleOutputPort.
                        searchInvestmentVehicle("g",fundGrowth,pageSize,pageNumber);
            }catch (MeedlException exception){
                log.info("{} {}", exception.getClass().getName(), exception.getMessage());
            }
            assertNotNull(investmentVehicles);
            assertEquals(1,investmentVehicles.getContent().size());
    }


    @Order(5)
    @Test
    void investmentVehicleCanBeSavedToDraft(){
        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("Save To Draft");
        investmentVehicle.setInvestmentVehicleStatus(DRAFT);
        investmentVehicle.setSize(null);
        investmentVehicle.setInterestRateOffered(null);
        try{
            investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
            draftInvestmentVehicleId = investmentVehicle.getId();
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicle);
        assertEquals(DRAFT, investmentVehicle.getInvestmentVehicleStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void viewInvestmentVehicleDetailWithInvalidLink(String link){
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findByInvestmentVehicleLink(link));
    }
    @Order(6)
    @Test
    void viewAllInvestmentVehicleByType() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            InvestmentVehicle savedVehicle = investmentVehicleOutputPort.save(fundGrowth);
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            commercialInvestmentId = savedVehicle.getId();
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
`        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleType().equals(InvestmentVehicleType.ENDOWMENT));
    }
    @Test
    void viewAllInvestmentVehicleByTypeCommercial() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.COMMERCIAL);
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleType().equals(InvestmentVehicleType.COMMERCIAL));
    }

    @Test
    void viewAllInvestmentVehicleByTypeDoesNotReturnDraft() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle -> investmentVehicle.getInvestmentVehicleType().equals(InvestmentVehicleType.ENDOWMENT));
        assertThat(investmentVehicles).allMatch(investmentVehicle -> investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED));
    }

    @Test
    void viewAllInvestmentVehicleByStatus() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize, pageNumber, InvestmentVehicleStatus.PUBLISHED);
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED));
    }

    @Test
    void viewAllInvestmentVehicleByStatusReturnDraft() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            InvestmentVehicle savedVehicle = investmentVehicleOutputPort.save(fundGrowth);
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize, pageNumber, DRAFT);
            investmentVehicleOutputPort.deleteInvestmentVehicle(savedVehicle.getId());
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(DRAFT));
    }

    @Test
    void viewAllInvestmentVehicleByStatusThrowExceptionForNullParameter(){
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize, pageNumber,null));
    }

    @Test
    void viewAllInvestmentVehicleByTypeThrowExceptionForNullParameter() {
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber, null));
    }


    @Test
    void viewAllInvestmentVehiclePassingNullParameter(){
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber,null));
    }

    @Test
    void viewAllInvestmentVehicleByTypeAndStatus() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT, InvestmentVehicleStatus.PUBLISHED);
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleType().equals(InvestmentVehicleType.ENDOWMENT));
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED));
    }

    @Test
    void viewAllInvestmentVehicleByFundRaisingStatus() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try{
            InvestmentVehicle savedVehicle = investmentVehicleOutputPort.save(fundGrowth);
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByFundRaisingStatus(
                    pageSize, pageNumber, FundRaisingStatus.FUND_RAISING);
            fundRaisingInvestmentId = savedVehicle.getId();
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.getContent().isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getFundRaisingStatus().equals(FundRaisingStatus.FUND_RAISING));

    }

    @Test
    void viewAllInvestmentVehicleByFundRaisingStatusReturnOnlyClosed(){
        Page<InvestmentVehicle> investmentVehicles = null;
        try{
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByFundRaisingStatus(
                    pageSize, pageNumber, FundRaisingStatus.CLOSED);
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.getContent().isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getFundRaisingStatus().equals(FundRaisingStatus.CLOSED));
    }

    @Test
    void viewAllInvestmentVehicleByFundRaisingStatusWithNullValue(){
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByFundRaisingStatus(pageSize, pageNumber, null));
    }

    @Test
    void viewAllInvestmentVehicleByTypeCommercialAndStatusDraft() {
        Page<InvestmentVehicle> investmentVehicles = null;
        try {
            InvestmentVehicle savedVehicle = investmentVehicleOutputPort.save(seaGrowth);
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(
                    pageSize, pageNumber, InvestmentVehicleType.COMMERCIAL, DRAFT);
            seaGrowthInvestmentVehicleId = savedVehicle.getId();
        } catch (Exception exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.getContent().isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleType().equals(InvestmentVehicleType.COMMERCIAL));
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(DRAFT));
    }

    @Test
    void viewAllInvestmentVehicleByTypeAndStatusNullParameterInType() {
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(pageSize, pageNumber, null, InvestmentVehicleStatus.PUBLISHED));
    }

    @Test
    void viewAllInvestmentVehicleByTypeAndStatusNullParameterInStatus() {
        assertThrows(MeedlException.class, ()->investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT, null));
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(draftInvestmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(seaGrowthInvestmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(fundRaisingInvestmentId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(commercialInvestmentId);
    }

}
