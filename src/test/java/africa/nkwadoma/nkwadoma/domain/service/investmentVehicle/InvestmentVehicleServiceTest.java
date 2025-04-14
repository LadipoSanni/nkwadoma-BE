package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.VehicleOperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class InvestmentVehicleServiceTest {
    @InjectMocks
    private InvestmentVehicleService investmentVehicleService;
    private InvestmentVehicle fundGrowth;
    private String investmentId;
    @Mock
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private int pageSize = 1;
    private int pageNumber = 0;
    private Financier financier;
    private UserIdentity userIdentity;
    private String financierId;
    private InvestmentVehicleFinancier investmentVehicleFinancier;
    @Mock
    private FinancierOutputPort financierOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    @Mock
    private PortfolioOutputPort portfolioOutputPort;
    private Portfolio portfolio;
    private String investmentVehicleId = "f593a10f-6854-44d4-acc2-259065d3e5c8";
    private String testFinancierId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private String nonExistingVehicleId = "f593a10f-6854-44d4-acc2-259065d3e5c8";
    private VehicleOperation vehicleOperation;
    @Mock
    private VehicleOperationOutputPort vehicleOperationOutputPort;
    @Mock
    private CouponDistributionOutputPort couponDistributionOutputPort;
    private CouponDistribution couponDistribution;
    @Mock
    private InvestmentVehicleEntityRepository investmentVehicleRepository;

    @Mock
    private InvestmentVehicleMapper investmentVehicleMapper;
    @Mock
    private VehicleOperationMapper vehicleOperationMapper;


    @BeforeEach
    void setUp() {
        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment limited");
        userIdentity = TestData.createTestUserIdentity("iniestajnr12@gmail.com");
        financier = TestData.buildFinancierIndividual(userIdentity);
        investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancier(financier,fundGrowth);
        portfolio = TestData.createMeedlPortfolio();
        vehicleOperation = TestData.createVehicleOperation(null);
        couponDistribution = TestData.createCouponDistribution();

    }

    @Test
    void setUpInvestmentVehicle() {
        InvestmentVehicle createdInvestmentVehicle = new InvestmentVehicle();
        try {
           when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
            createdInvestmentVehicle =
                   investmentVehicleService.setUpInvestmentVehicle(fundGrowth);
           investmentId = createdInvestmentVehicle.getId();
       }catch (MeedlException exception){
           log.info("{} {}",exception.getClass().getName(), exception.getMessage());
       }
        assertNotNull(createdInvestmentVehicle);
    }

    @Test
    void viewNonExistentInvestmentVehicle() {
        String nonExistingId = "f593a10f-6854-44d4-acc2-259065d3e5c8";
        String validFinancierId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
        UserIdentity mockUser = UserIdentity.builder()
                .role(IdentityRole.FINANCIER)
                .build();
        try {
            when(userIdentityOutputPort.findById(validFinancierId)).thenReturn(mockUser);
            Financier mockFinancier = Financier.builder()
                    .id(validFinancierId)
                    .build();
            when(financierOutputPort.findFinancierByUserId(validFinancierId)).thenReturn(mockFinancier);
            when(investmentVehicleOutputPort.findById(nonExistingId))
                    .thenThrow(new MeedlException("Investment vehicle not found"));
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        Exception exception = assertThrows(MeedlException.class, () ->
                investmentVehicleService.viewInvestmentVehicleDetails(nonExistingId, validFinancierId));
        assertEquals("Investment vehicle not found", exception.getMessage());
    }

    @Test
    void viewAllInvestmentVehicle() throws MeedlException {
        when(investmentVehicleOutputPort.findAllInvestmentVehicle(pageSize,pageNumber)).
                thenReturn(new PageImpl<>(List.of(fundGrowth)));
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        Page<InvestmentVehicle> investmentVehicles = investmentVehicleService.viewAllInvestmentVehicle(
                mockId,pageSize, pageNumber);
        List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
        assertEquals(1, investmentVehiclesList.size());
    }

    @Test
    void viewAllInvestmentVehiclesByType(){
        List<InvestmentVehicle> investmentVehiclesList = new ArrayList<>();
        try{
            when(investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber,InvestmentVehicleType.ENDOWMENT))
                    .thenReturn(new PageImpl<>(List.of(fundGrowth)));
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleService.viewAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            investmentVehiclesList = investmentVehicles.toList();
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(1, investmentVehiclesList.size());
    }

    @Test
    void viewAllInvestmentVehiclesByStatus(){
        Page<InvestmentVehicle> investmentVehicles = null;
        try{
            when(investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize,pageNumber,PUBLISHED))
                    .thenReturn(new PageImpl<>(List.of(fundGrowth)));
            investmentVehicles = investmentVehicleService.viewAllInvestmentVehicleByStatus(pageSize, pageNumber, InvestmentVehicleStatus.PUBLISHED);
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicles);
        assertFalse(investmentVehicles.isEmpty());
        assertThat(investmentVehicles).allMatch(investmentVehicle-> investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED));
    }

    @Test
    void viewAllInvestmentVehiclesByTypeAndStatus(){
        try{
            when(investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize,pageNumber,InvestmentVehicleType.ENDOWMENT))
                    .thenReturn(new PageImpl<>(List.of(fundGrowth)));
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleService.viewAllInvestmentVehicleByType(
                    pageSize, pageNumber, InvestmentVehicleType.ENDOWMENT);
            List<InvestmentVehicle> investmentVehiclesList = investmentVehicles.toList();
            assertEquals(1, investmentVehiclesList.size());
        } catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void cannotSetVisibilityWithNullInvestmentVehicleId(){
        assertThrows(MeedlException.class , () -> investmentVehicleService.setInvestmentVehicleVisibility(null,
                InvestmentVehicleVisibility.PUBLIC,List.of()));
    }

    @Test
    void cannotSetVisibilityWithNullInvestmentVehicleVisibility(){
        assertThrows(MeedlException.class , () -> investmentVehicleService.setInvestmentVehicleVisibility(investmentId,
                null,List.of()));
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jhhfvh9394-k"})
    void cannotSetVisibilityWithEmptyOrInvalidInvestmentVehicleId(String id){
        assertThrows(MeedlException.class , () -> investmentVehicleService.setInvestmentVehicleVisibility(id,
                InvestmentVehicleVisibility.PUBLIC,List.of()));
    }

    @Test
    void setUpInvestmentVehicleVisibilityToPublic(){
        InvestmentVehicle investmentVehicle = null;
        try {
            fundGrowth.setId(mockId);
            when(investmentVehicleOutputPort.findById(fundGrowth.getId())).thenReturn(fundGrowth);
            fundGrowth.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PUBLIC);
            when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
            investmentVehicle = investmentVehicleService.setInvestmentVehicleVisibility(fundGrowth.getId(),
                    InvestmentVehicleVisibility.PUBLIC,List.of());
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(investmentVehicle);
        assertEquals(InvestmentVehicleVisibility.PUBLIC,investmentVehicle.getInvestmentVehicleVisibility());
    }

    @Test
    void setUpInvestmentVehicleVisibilityToPrivate(){
        InvestmentVehicle investmentVehicle = null;
        try {
            financier.setId(mockId);
            fundGrowth.setId(mockId);
            when(investmentVehicleOutputPort.findById(fundGrowth.getId())).thenReturn(fundGrowth);
            fundGrowth.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PRIVATE);
            when(financierOutputPort.findFinancierByFinancierId(financier.getId())).thenReturn(financier);
            when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
            when(portfolioOutputPort.findPortfolio(Portfolio.builder().portfolioName("Meedl").build())).thenReturn(portfolio);
//            verify(investmentVehicleOutputPort, times(1)).save(fundGrowth);
            investmentVehicle = investmentVehicleService.setInvestmentVehicleVisibility(fundGrowth.getId(),
                    InvestmentVehicleVisibility.PRIVATE,List.of(financier));
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
            when(investmentVehicleOutputPort.findAllInvestmentVehicleByFundRaisingStatus(pageSize,pageNumber,
                    FundRaisingStatus.FUND_RAISING)).thenReturn(new PageImpl<>(List.of(fundGrowth)));
            Page<InvestmentVehicle> investmentVehicles = investmentVehicleService.viewAllInvestmentVehicleByFundRaisingStatus(
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
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewAllInvestmentVehicleByStatus(pageSize, pageNumber,null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid"})
    void accessInvestmentVehicleWithInvalidVehicleId(String investmentVehicleId){
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, financierId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid"})
    void viewInvestmentVehicleWithInvalidFinancierId(String financierId){
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewInvestmentVehicleDetails(investmentId, financierId));
    }

    @Test
    void viewInvestmentVehicleWithFinancierIdThatDoesNotExistInTheDB() {
        String nonExistingFinancierId = "f593a10f-6854-44d4-acc2-259065d3e5c8";
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewInvestmentVehicleDetails(investmentId, nonExistingFinancierId));
    }

    @Test
    void viewInvestmentVehicleWithInvestmentVehicleIdThatDoesNotExistInTheDB() {
        UserIdentity mockUser = UserIdentity.builder()
                .role(IdentityRole.FINANCIER)
                .build();
        try {
            when(userIdentityOutputPort.findById(testFinancierId)).thenReturn(mockUser);
            Financier mockFinancier = Financier.builder()
                    .id(testFinancierId)
                    .build();
            when(financierOutputPort.findFinancierByUserId(testFinancierId)).thenReturn(mockFinancier);
            doThrow(new MeedlException("Investment vehicle not found"))
                    .when(investmentVehicleOutputPort).findById(nonExistingVehicleId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        Exception exception = assertThrows(MeedlException.class,
                () -> investmentVehicleService.viewInvestmentVehicleDetails(nonExistingVehicleId, testFinancierId));
        assertEquals("Investment vehicle not found", exception.getMessage());
    }

    @Test
    void accessPrivateInvestmentVehicleThatFinancierIsNotPartOf() {
        InvestmentVehicle privateVehicle = InvestmentVehicle.builder()
                .id(investmentVehicleId)
                .investmentVehicleVisibility(InvestmentVehicleVisibility.PRIVATE)
                .build();
        try{
            when(investmentVehicleOutputPort.findById(investmentVehicleId))
                    .thenReturn(privateVehicle);
            UserIdentity mockUser = UserIdentity.builder()
                    .role(IdentityRole.FINANCIER)
                    .build();
            when(userIdentityOutputPort.findById(testFinancierId))
                    .thenReturn(mockUser);
            Financier mockFinancier = Financier.builder()
                    .id(testFinancierId)
                    .build();
            when(financierOutputPort.findFinancierByUserId(testFinancierId))
                    .thenReturn(mockFinancier);

            when(investmentVehicleFinancierOutputPort
                    .findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, testFinancierId))
                    .thenReturn(Optional.empty());
            Exception exception = assertThrows(MeedlException.class,
                    () -> investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId));
            assertEquals("Investment Vehicle not found", exception.getMessage());

            verify(investmentVehicleOutputPort).findById(investmentVehicleId);
            verify(userIdentityOutputPort).findById(testFinancierId);
            verify(financierOutputPort).findFinancierByUserId(testFinancierId);
            verify(investmentVehicleFinancierOutputPort)
                    .findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, testFinancierId);
        } catch (MeedlException exception) {
            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void accessPublicInvestmentVehicle() throws MeedlException {
        InvestmentVehicle publicVehicle = InvestmentVehicle.builder()
                .id(investmentVehicleId)
                .investmentVehicleVisibility(InvestmentVehicleVisibility.PUBLIC)
                .build();
        when(investmentVehicleOutputPort.findById(investmentVehicleId))
                .thenReturn(publicVehicle);
        UserIdentity mockUser = UserIdentity.builder()
                .role(IdentityRole.FINANCIER)
                .build();
        when(userIdentityOutputPort.findById(testFinancierId))
                .thenReturn(mockUser);
        Financier mockFinancier = Financier.builder()
                .id(testFinancierId)
                .build();
        when(financierOutputPort.findFinancierByUserId(testFinancierId))
                .thenReturn(mockFinancier);
        InvestmentVehicle result = investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId);
        assertNotNull(result);
        assertEquals(InvestmentVehicleVisibility.PUBLIC, result.getInvestmentVehicleVisibility());
    }

    @Test
    void accessPrivateInvestmentVehicleThatFinancierIsPartOf() throws MeedlException {
        InvestmentVehicle privateVehicle = InvestmentVehicle.builder()
                .id(investmentVehicleId)
                .investmentVehicleVisibility(InvestmentVehicleVisibility.PRIVATE)
                .build();
        when(investmentVehicleOutputPort.findById(investmentVehicleId))
                .thenReturn(privateVehicle);
        UserIdentity mockUser = UserIdentity.builder()
                .role(IdentityRole.FINANCIER)
                .build();
        when(userIdentityOutputPort.findById(testFinancierId))
                .thenReturn(mockUser);
        Financier mockFinancier = Financier.builder()
                .id(testFinancierId)
                .build();
        when(financierOutputPort.findFinancierByUserId(testFinancierId))
                .thenReturn(mockFinancier);
        InvestmentVehicleFinancier investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancier(mockFinancier, privateVehicle);
        when(investmentVehicleFinancierOutputPort
                .findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, testFinancierId))
                .thenReturn(Optional.of(investmentVehicleFinancier));
        InvestmentVehicle result = investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId);
        assertNotNull(result);
        assertEquals(InvestmentVehicleVisibility.PRIVATE, result.getInvestmentVehicleVisibility());
    }

    @Test
    void financierAccessingDefaultInvestmentVehicle() throws MeedlException {
        UserIdentity mockUser = UserIdentity.builder()
                .role(IdentityRole.FINANCIER)
                .build();
        when(userIdentityOutputPort.findById(testFinancierId))
                .thenReturn(mockUser);
        InvestmentVehicle defaultVehicle = InvestmentVehicle.builder()
                .id(investmentVehicleId)
                .investmentVehicleVisibility(InvestmentVehicleVisibility.DEFAULT)
                .build();
        when(investmentVehicleOutputPort.findById(investmentVehicleId))
                .thenReturn(defaultVehicle);
        Financier mockFinancier = Financier.builder().id(testFinancierId).build();
            when(financierOutputPort.findFinancierByUserId(testFinancierId))
                    .thenReturn(mockFinancier);
        assertThrows(MeedlException.class, () ->
                investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId));
    }

    @Test
    void portfolioManagerCanAccessDefaultInvestmentVehicle() throws MeedlException {
        String portfolioManagerId = UUID.randomUUID().toString();
        UserIdentity portfolioManager = UserIdentity.builder()
                .role(IdentityRole.PORTFOLIO_MANAGER)
                .build();
        when(userIdentityOutputPort.findById(portfolioManagerId))
                .thenReturn(portfolioManager);
        InvestmentVehicle defaultVehicle = InvestmentVehicle.builder()
                .id(investmentVehicleId)
                .investmentVehicleVisibility(InvestmentVehicleVisibility.DEFAULT)
                .build();
        when(investmentVehicleOutputPort.findById(investmentVehicleId))
                .thenReturn(defaultVehicle);
        InvestmentVehicle investmentVehicle = investmentVehicleService.viewInvestmentVehicleDetails(
                investmentVehicleId,
                portfolioManagerId);
        assertNotNull(investmentVehicle);
        assertEquals(InvestmentVehicleVisibility.DEFAULT, investmentVehicle.getInvestmentVehicleVisibility());
    }

    @Test
    void setInvestmentVehicleOperationStatus() {
        fundGrowth.setId(mockId);
        fundGrowth.setVehicleOperation(vehicleOperation);
        fundGrowth.getVehicleOperation().setDeployingStatus(null);
        try {
            when(investmentVehicleOutputPort.findById(fundGrowth.getId())).thenReturn(TestData.buildInvestmentVehicle("Name"));
            when(vehicleOperationOutputPort.save(fundGrowth.getVehicleOperation())).thenReturn(vehicleOperation);
            when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
            when(investmentVehicleOutputPort.findById(fundGrowth.getId())).
                    thenReturn(fundGrowth);
            when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
            fundGrowth = investmentVehicleService.setInvestmentVehicleOperationStatus(fundGrowth);
        } catch (MeedlException meedlException) {
            log.info("{} {}", meedlException.getClass().getName(), meedlException.getMessage());
        }
        assertNotNull(fundGrowth);
        assertEquals(vehicleOperation, fundGrowth.getVehicleOperation());
        assertEquals(vehicleOperation.getFundRaisingStatus(), fundGrowth.getVehicleOperation().getFundRaisingStatus());
    }

    @Test
    void cannotSetBothFundRaisingAndDeployingStatus() {
        fundGrowth.setId(mockId);
        fundGrowth.setVehicleOperation(vehicleOperation);
        assertThrows(MeedlException.class, ()-> investmentVehicleService.setInvestmentVehicleOperationStatus(fundGrowth));
    }

    @Test
    void bothFundRaisingAndDeployingStatusCannotBeNull() {
        fundGrowth.setId(mockId);
        vehicleOperation.setDeployingStatus(null);
        vehicleOperation.setFundRaisingStatus(null);
        fundGrowth.setVehicleOperation(vehicleOperation);
        assertThrows(MeedlException.class, ()-> investmentVehicleService.setInvestmentVehicleOperationStatus(fundGrowth));
    }

    @Test
    void deleteInvestmentVehicleSavedInDraftWithNullId() {
        assertThrows(MeedlException.class, () -> investmentVehicleService.deleteInvestmentVehicle(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jdhhfjh=djdj"})
    void deleteInvestmentVehicleSavedInDraftWithInvalidId(String invalidId) {
        assertThrows(MeedlException.class, () -> investmentVehicleService.deleteInvestmentVehicle(invalidId));
    }

    @Test
    void cannotDeleteFinalizeInvestmentVehiclePublishing() throws MeedlException {
        when(investmentVehicleOutputPort.findById(mockId)).thenReturn(fundGrowth);
        assertThrows(MeedlException.class, () -> investmentVehicleService.deleteInvestmentVehicle(mockId));
        verify(investmentVehicleOutputPort, times(1)).findById(mockId);
        verify(investmentVehicleOutputPort, never()).deleteInvestmentVehicle(mockId);
    }

    @Test
    void deleteInvestmentVehicleInDraft() throws MeedlException {
        fundGrowth.setInvestmentVehicleStatus(DRAFT);
        when(investmentVehicleOutputPort.findById(mockId)).thenReturn(fundGrowth);
        String response = investmentVehicleService.deleteInvestmentVehicle(mockId);
        assertNotNull(response);
        verify(investmentVehicleOutputPort, times(1)).findById(mockId);
        verify(investmentVehicleOutputPort, times(1)).deleteInvestmentVehicle(mockId);
        verifyNoMoreInteractions(investmentVehicleOutputPort);
    }

}
