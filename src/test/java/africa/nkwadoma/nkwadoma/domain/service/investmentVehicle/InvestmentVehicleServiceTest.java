package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.InvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class InvestmentVehicleServiceTest {
    @Autowired
    private InvestmentVehicleUseCase investmentVehicleUseCase;
    private InvestmentVehicle fundGrowth;
    private String investmentId;
    @Autowired
    private InvestmentVehicleOutputPort outputPort;
    private int pageSize = 1;
    private int pageNumber = 0;
    private Financier financier;
    private UserIdentity userIdentity;
    private String financierId;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;

    @BeforeAll
    void setUp() {
        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment limited");
        userIdentity = TestData.createTestUserIdentity("iniestajnr12@gmail.com");
        try {
            userIdentity = userIdentityOutputPort.save(userIdentity);
            financier = TestData.buildFinancierIndividual(userIdentity);
            financier = financierOutputPort.save(financier);
            financierId = financier.getId();
        }catch (MeedlException e) {
            log.error(e.getMessage());
        }
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

    @Test
    @Order(5)
    void viewAllInvestmentVehiclesByStatus(){
        Page<InvestmentVehicle> investmentVehicles = null;
        try{
            investmentVehicles = investmentVehicleUseCase.viewAllInvestmentVehicleByStatus(pageSize, pageNumber, InvestmentVehicleStatus.PUBLISHED);
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED));
    }

    @Order(6)
    @Test
    void viewAllInvestmentVehiclesByTypeAndStatus(){
        try{
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase.viewAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
            assertEquals(1, investmentVehiclesList.size());
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void cannotSetVisibilityWithNullInvestmentVehicleId(){
        assertThrows(MeedlException.class , () -> investmentVehicleUseCase.setInvestmentVehicleVisibility(null,
                InvestmentVehicleVisibility.PUBLIC,List.of()));
    }

    @Test
    void cannotSetVisibilityWithNullInvestmentVehicleVisibility(){
        assertThrows(MeedlException.class , () -> investmentVehicleUseCase.setInvestmentVehicleVisibility(investmentId,
                null,List.of()));
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jhhfvh9394-k"})
    void cannotSetVisibilityWithEmptyOrInvalidInvestmentVehicleId(String id){
        assertThrows(MeedlException.class , () -> investmentVehicleUseCase.setInvestmentVehicleVisibility(id,
                InvestmentVehicleVisibility.PUBLIC,List.of()));
    }


    @Order(7)
    @Test
    void setUpInvestmentVehicleVisibilityToPublic(){
        InvestmentVehicle investmentVehicle = null;
        try {
             investmentVehicle = investmentVehicleUseCase.setInvestmentVehicleVisibility(investmentId,
                    InvestmentVehicleVisibility.PUBLIC,List.of());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicle);
        assertEquals(InvestmentVehicleVisibility.PUBLIC,investmentVehicle.getInvestmentVehicleVisibility());
    }

    @Order(8)
    @Test
    void setUpInvestmentVehicleVisibilityToPrivate(){
        InvestmentVehicle investmentVehicle = null;
        try {
            investmentVehicle = investmentVehicleUseCase.setInvestmentVehicleVisibility(investmentId,
                    InvestmentVehicleVisibility.PRIVATE,List.of(financier.getId()));
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicle);
        assertEquals(InvestmentVehicleVisibility.PRIVATE,investmentVehicle.getInvestmentVehicleVisibility());
    }


    @Test
    void viewAllInvestmentVehiclesByFundRaisingStatus(){
        List<InvestmentVehicle> investmentVehicleList = new ArrayList<>();
        try {
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase.viewAllInvestmentVehicleByFundRaisingStatus(
                    pageSize, pageNumber, FundRaisingStatus.FUND_RAISING);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
        } catch (MeedlException exception) {
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicleList);
        assertThat(investmentVehicleList).allMatch(investmentVehicle-> investmentVehicle.getFundRaisingStatus().equals(FundRaisingStatus.FUND_RAISING));
    }


    @Test
    void viewAllInvestmentVehiclesByStatusWithNullParameter(){
        assertThrows(MeedlException.class, ()->investmentVehicleUseCase.viewAllInvestmentVehicleByStatus(pageSize, pageNumber,null));
    }

    @AfterAll
    void cleanUp() {
        try {
            investmentVehicleFinancierOutputPort.deleteByInvestmentVehicleIdAndFinancierId(investmentId,financierId);
            investmentVehicleUseCase.deleteInvestmentVehicle(investmentId);
            financierOutputPort.delete(financier.getId());
        }catch (MeedlException exception){
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

}
