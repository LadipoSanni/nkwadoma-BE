package africa.nkwadoma.nkwadoma.domain.service.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.VehicleOperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.PUBLISHED;
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
    private VehicleClosure vehicleClosure;
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
    @Mock
    private VehicleClosureOutputPort vehicleClosureOutputPort;



    @BeforeEach
    void setUp() {
        fundGrowth = TestData.buildInvestmentVehicle("Growth Investment limited");
        userIdentity = TestData.createTestUserIdentity("iniestajnr12@gmail.com");
        financier = TestData.buildFinancierIndividual(userIdentity);
        investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancier(financier,fundGrowth);
        portfolio = TestData.createMeedlPortfolio();
        vehicleOperation = TestData.createVehicleOperation(null);
        vehicleClosure = TestData.buildVehicleClosure(TestData.buildCapitalDistribution());
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
                .id(validFinancierId)
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
    void setUpInvestmentVehicleVisibilityToPublic() throws MeedlException {
        InvestmentVehicle result = null;
        when(investmentVehicleOutputPort.findById(mockId)).thenReturn(fundGrowth);
        fundGrowth.setInvestmentVehicleVisibility(null);
        when(investmentVehicleOutputPort.findByNameExcludingDraftStatus(fundGrowth.getName(), DRAFT))
                .thenReturn(null);
        when(portfolioOutputPort.findPortfolio(any()))
                .thenReturn(portfolio);
        fundGrowth.setInvestmentVehicleStatus(DRAFT);
        when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
        try {
            result = investmentVehicleService.setInvestmentVehicleVisibility(
                    mockId,
                    InvestmentVehicleVisibility.PUBLIC,
                    List.of()
            );
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(result);
        assertEquals(InvestmentVehicleVisibility.PUBLIC, result.getInvestmentVehicleVisibility());
        assertEquals(PUBLISHED, result.getInvestmentVehicleStatus());
    }


    @Test
    void setUpInvestmentVehicleVisibilityToPrivate() throws MeedlException {
        InvestmentVehicle result = null;
        financier.setId(mockId);
        when(investmentVehicleOutputPort.findById(mockId)).thenReturn(fundGrowth);
        fundGrowth.setInvestmentVehicleVisibility(null);
        when(investmentVehicleOutputPort.findByNameExcludingDraftStatus(fundGrowth.getName(), DRAFT))
                .thenReturn(null);
        when(financierOutputPort.findById(mockId)).thenReturn(financier);
        when(portfolioOutputPort.findPortfolio(any()))
                .thenReturn(portfolio);
        fundGrowth.setInvestmentVehicleStatus(DRAFT);
        when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
        try {
            result = investmentVehicleService.setInvestmentVehicleVisibility(
                    mockId,
                    InvestmentVehicleVisibility.PRIVATE,
                    List.of(financier)
            );
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertNotNull(result);
        assertEquals(InvestmentVehicleVisibility.PRIVATE, result.getInvestmentVehicleVisibility());
        assertEquals(PUBLISHED, result.getInvestmentVehicleStatus());
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
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewPublicInvestmentVehicleWithInvalidLink(String investmentVehicleLink){
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewInvestmentVehicleDetailsViaLink(investmentVehicleLink));
    }
    @Test
    void viewPrivateInvestmentVehicleViaLink(){
        String link = "link";
        try {
            when(investmentVehicleOutputPort.findByInvestmentVehicleLink(link)).thenReturn(fundGrowth);
            assertThrows(MeedlException.class, ()-> investmentVehicleService.viewInvestmentVehicleDetailsViaLink(link));
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void viewPublicInvestmentVehicleViaLink(){
        String link = "link";
        try {
            fundGrowth.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PUBLIC);
            when(investmentVehicleOutputPort.findByInvestmentVehicleLink(link)).thenReturn(fundGrowth);
            InvestmentVehicle investmentVehicle = investmentVehicleService.viewInvestmentVehicleDetailsViaLink(link);
            assertNotNull(investmentVehicle);
        } catch (MeedlException e) {
            log.warn("Failed ",e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void viewInvestmentVehicleWithFinancierIdThatDoesNotExistInTheDB() {
        String nonExistingFinancierId = "f593a10f-6854-44d4-acc2-259065d3e5c8";
        assertThrows(MeedlException.class, ()->investmentVehicleService.viewInvestmentVehicleDetails(investmentId, nonExistingFinancierId));
    }

    @Test
    void viewInvestmentVehicleWithInvestmentVehicleIdThatDoesNotExistInTheDB() {
        UserIdentity mockUser = UserIdentity.builder()
                .id(testFinancierId)
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
                    .id(testFinancierId)
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
                    .findByAll(investmentVehicleId, testFinancierId))
                    .thenReturn(List.of());
            Exception exception = assertThrows(MeedlException.class,
                    () -> investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId));
            assertEquals("Investment Vehicle not found", exception.getMessage());

            verify(investmentVehicleOutputPort).findById(investmentVehicleId);
            verify(userIdentityOutputPort).findById(testFinancierId);
            verify(financierOutputPort).findFinancierByUserId(testFinancierId);
            verify(investmentVehicleFinancierOutputPort)
                    .findByAll(investmentVehicleId, testFinancierId);
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
                .id(testFinancierId)
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
                .id(testFinancierId)
                .role(IdentityRole.FINANCIER)
                .build();
        when(userIdentityOutputPort.findById(testFinancierId))
                .thenReturn(mockUser);
        Financier mockFinancier = Financier.builder()
                .id(testFinancierId)
                .financierType(FinancierType.INDIVIDUAL)
                .build();
        when(financierOutputPort.findFinancierByUserId(testFinancierId))
                .thenReturn(mockFinancier);
        InvestmentVehicleFinancier investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancier(mockFinancier, privateVehicle);
        when(investmentVehicleFinancierOutputPort
                .findByAll(investmentVehicleId, testFinancierId))
                .thenReturn(List.of(investmentVehicleFinancier));
        InvestmentVehicle result = investmentVehicleService.viewInvestmentVehicleDetails(investmentVehicleId, testFinancierId);
        assertNotNull(result);
        assertEquals(InvestmentVehicleVisibility.PRIVATE, result.getInvestmentVehicleVisibility());
    }

    @Test
    void financierAccessingDefaultInvestmentVehicle() throws MeedlException {
        UserIdentity mockUser = UserIdentity.builder()
                .id(testFinancierId)
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
        fundGrowth.getVehicleOperation().setCouponDistributionStatus(null);
        vehicleClosure.setRecollectionStatus(null);
        vehicleClosure.setMaturity(null);
        fundGrowth.setVehicleClosureStatus(vehicleClosure);

        try {
            when(investmentVehicleOutputPort.findById(fundGrowth.getId())).thenReturn(fundGrowth);
            when(vehicleOperationOutputPort.save(fundGrowth.getVehicleOperation())).thenReturn(vehicleOperation);
            when(investmentVehicleOutputPort.save(fundGrowth)).thenReturn(fundGrowth);
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
        fundGrowth.setVehicleClosureStatus(vehicleClosure);
        assertThrows(MeedlException.class, ()-> investmentVehicleService.setInvestmentVehicleOperationStatus(fundGrowth));
    }

    @Test
    void allStatusCannotBeNull() {
        fundGrowth.setId(mockId);
        vehicleOperation.setDeployingStatus(null);
        vehicleOperation.setFundRaisingStatus(null);
        vehicleOperation.setCouponDistributionStatus(null);
        vehicleClosure.setRecollectionStatus(null);
        vehicleClosure.setMaturity(null);
        fundGrowth.setVehicleOperation(vehicleOperation);
        fundGrowth.setVehicleClosureStatus(vehicleClosure);
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

    @Test
    void viewAllInvestmentVehicleAddedTo() throws MeedlException {
        Page<InvestmentVehicle> page = new PageImpl<>(List.of(fundGrowth));
        userIdentity.setId(mockId);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(investmentVehicleOutputPort.findAllInvestmentVehicleFinancierWasAddedTo(userIdentity.getId(), InvestmentVehicleType.ENDOWMENT,pageSize,pageNumber)).
                thenReturn(page);
        page = investmentVehicleService.viewAllInvestmentVehicleInvestedIn(mockId,mockId,InvestmentVehicleType.ENDOWMENT,pageSize,pageNumber);
        assertNotNull(page);
        verify(userIdentityOutputPort, times(1)).findById(mockId);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void searchMyInvestmentVehicle() throws MeedlException {
        Page<InvestmentVehicle> page = new PageImpl<>(List.of(fundGrowth));
        userIdentity.setId(mockId);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(investmentVehicleOutputPort.searchInvestmentVehicleFinancierWasAddedTo(userIdentity.getId(),fundGrowth,pageSize,pageNumber)).
                thenReturn(page);
        page = investmentVehicleService.searchMyInvestment(mockId,fundGrowth,pageSize,pageNumber);
        assertNotNull(page);
        verify(userIdentityOutputPort, times(1)).findById(mockId);
        assertEquals(1, page.getTotalElements());
    }

}
