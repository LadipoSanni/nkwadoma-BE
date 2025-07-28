    package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.ClientRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganizationIdentityServiceTest {
    @InjectMocks
    private OrganizationIdentityService organizationIdentityService;
    @Mock
    private IdentityManagerOutputPort identityManagerOutPutPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private OrganizationIdentityMapper organizationIdentityMapper;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private OrganizationEntityRepository organizationEntityRepository;
    @Mock
    private OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    @Mock
    private LoanMetricsUseCase loanMetricsUseCase;
    @Mock
    private AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    @Mock
    private AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private UserIdentity sarah;
    private OrganizationIdentity roseCouture;
    private OrganizationEmployeeIdentity employeeSarah;
    private List<OrganizationEmployeeIdentity> orgEmployee;
    private final String mockId = "83f744df-78a2-4db6-bb04-b81545e78e49";
    private int pageSize = 10;
    private int pageNumber = 0;
    @Mock
    private OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;

    @BeforeEach
    void setUp() {
        sarah = new UserIdentity();
        sarah.setRole(IdentityRole.PORTFOLIO_MANAGER);
        sarah.setId(mockId);
        sarah.setFirstName("Sarah");
        sarah.setLastName("Jacobs");
        sarah.setEmail("divinemercy601@gmail.com");
        sarah.setCreatedBy(mockId);

        employeeSarah = new OrganizationEmployeeIdentity();
        employeeSarah.setMeedlUser(sarah);
        OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setMeedlUser(sarah);

        orgEmployee = new ArrayList<>();
        orgEmployee.add(employeeSarah);


        roseCouture = new OrganizationIdentity();
        roseCouture.setId("83f744df-78a2-4db6-bb04-b81545e78e49");
        roseCouture.setName("rose couture6");
        roseCouture.setEmail("iamoluchimercy@gmail.com");
        roseCouture.setTin("7682-5627");
        roseCouture.setRcNumber("RC8789905");
        roseCouture.setServiceOfferings(List.of(new ServiceOffering()));
        roseCouture.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        roseCouture.setPhoneNumber("09876365713");
        roseCouture.setInvitedDate(LocalDateTime.now().toString());
        roseCouture.setWebsiteAddress("rosecouture.org");
        roseCouture.setOrganizationEmployees(orgEmployee);
        roseCouture.setEnabled(Boolean.TRUE);


    }

    @Test
    void inviteOrganization() {
        OrganizationIdentity invitedOrganisation = new OrganizationIdentity();
        try {
            when(identityManagerOutPutPort.createKeycloakClient(roseCouture)).thenReturn(roseCouture);
            when(identityManagerOutPutPort.createUser(sarah)).thenReturn(sarah);
            when(organizationIdentityOutputPort.save(roseCouture)).thenReturn(roseCouture);
            when(userIdentityOutputPort.save(sarah)).thenReturn(sarah);
            when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
            when(identityManagerOutPutPort.getClientRepresentationByName(roseCouture.getName())).thenReturn(new ClientRepresentation());
            when(identityManagerOutPutPort.getUserByEmail(roseCouture.getOrganizationEmployees().get(0).getMeedlUser().getEmail())).thenReturn(Optional.empty());
            doNothing().when(asynchronousMailingOutputPort).sendEmailToInvitedOrganization(any(UserIdentity.class));
            doNothing().when(asynchronousNotificationOutputPort).notifyPortfolioManagerOfNewOrganization(any(OrganizationIdentity.class), any(NotificationFlag.class));
            when(loanMetricsUseCase.createLoanMetrics(anyString())).thenReturn(new LoanMetrics());

            OrganizationLoanDetail organizationLoanDetail = TestData.buildOrganizationLoanDetail(roseCouture);
            when(organizationLoanDetailOutputPort.save(any())).thenReturn(organizationLoanDetail);
            invitedOrganisation = organizationIdentityService.inviteOrganization(roseCouture);
            assertNotNull(invitedOrganisation);
            assertNotNull(invitedOrganisation.getServiceOfferings());
            assertEquals(roseCouture.getName(), invitedOrganisation.getName());
            assertEquals(ActivationStatus.INVITED, employeeSarah.getStatus());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void viewAllOrganizationsLoanRequests() throws MeedlException {
        Page<OrganizationIdentity> page = new PageImpl<>(List.of(roseCouture));
        when(organizationIdentityOutputPort.findAllWithLoanMetrics(LoanType.LOAN_REQUEST,pageSize,pageNumber))
                .thenReturn(page);
        Page<OrganizationIdentity> organizationIdentities = organizationIdentityService
                .viewAllOrganizationsLoanMetrics(LoanType.LOAN_REQUEST,pageSize,pageNumber);
        verify(organizationIdentityOutputPort, times(1)).
                findAllWithLoanMetrics(LoanType.LOAN_REQUEST,pageSize,pageNumber);
        assertNotNull(organizationIdentities);
        assertEquals(organizationIdentities.getContent().get(0).getId(), roseCouture.getId());
        assertEquals(organizationIdentities.getContent().get(0).getName(), roseCouture.getName());
        assertEquals(organizationIdentities.getContent().get(0).getLogoImage(), roseCouture.getLogoImage());
    }

    @Test
    void viewAllOrganizationWithStatusTakingNullParameter() {
        assertThrows(MeedlException.class, ()-> organizationIdentityService.viewAllOrganizationByStatus(roseCouture, null));
    }

    @Test
    void viewAllOrganizationWithStatus() throws MeedlException {

        OrganizationIdentity roseCouture2 = TestData.createOrganizationTestData("rose couture6", "RC8789905",orgEmployee);
        roseCouture2.setStatus(ActivationStatus.ACTIVE);

        int pageNumber = 0;
        int pageSize = 10;
        roseCouture.setPageNumber(pageNumber);
        roseCouture.setPageSize(pageSize);
        roseCouture.setStatus(ActivationStatus.ACTIVE);

        List<OrganizationIdentity> organizationIdentities = new ArrayList<>();
        organizationIdentities.add(roseCouture);
        organizationIdentities.add(roseCouture2);

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "invitedDate"));
        Page<OrganizationIdentity> organizationIdentityPage = new PageImpl<>(organizationIdentities, pageRequest, organizationIdentities.size());

        when(organizationIdentityOutputPort.viewAllOrganizationByStatus(roseCouture, ActivationStatus.ACTIVE)).thenReturn(organizationIdentityPage);
        Page<OrganizationIdentity> result = organizationIdentityService.viewAllOrganizationByStatus(roseCouture, ActivationStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(ActivationStatus.ACTIVE, result.getContent().get(1).getStatus());
        verify(organizationIdentityOutputPort, times(1)).viewAllOrganizationByStatus(roseCouture, ActivationStatus.ACTIVE);
    }

    @Test
    void inviteOrganizationWithEmptyOrganization() {
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(new OrganizationIdentity()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"WrongRcNumber", "RC123456", "OP1234567", "rc1234567", "123456789", "ABCDEFG"})
    void inviteOrganizationWithInvalidRCNumber(String rcNumber) {
        roseCouture.setRcNumber(rcNumber);
        assertThrows(MeedlException.class, ()-> organizationIdentityService.inviteOrganization(roseCouture));
    }

    @ParameterizedTest
    @ValueSource(strings = {"WrongTIN", "ABCDEFG"})
    void inviteOrganizationWithInvalidTIN(String tin) {
        roseCouture.setTin(tin);
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(roseCouture));
    }

    @Test
    void inviteOrganizationWithNullOrganization() {
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void inviteOrganizationWithInvalidEmail(String email){
        roseCouture.setEmail(email);
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(roseCouture));
    }


    @Test
    void inviteOrganizationWithNullEmail() {
        roseCouture.setEmail(null);
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(roseCouture));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void updateOrganizationWithInvalidOrganizationId(String orgId){
        roseCouture.setId(orgId);
        assertThrows(MeedlException.class, () -> organizationIdentityService.updateOrganization(roseCouture));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void updateOrganizationWithInvalidOrganizationUpdatedBy(String orgId){
        roseCouture.setUpdatedBy(orgId);
        assertThrows(MeedlException.class, () -> organizationIdentityService.updateOrganization(roseCouture));
    }
    @Test
    void updateOrganizationWithNewName(){
        roseCouture.setName("orgId");
        assertThrows(MeedlException.class, () -> organizationIdentityService.updateOrganization(roseCouture));
    }
    @Test
    void updateOrganizationWithNewRcNumber(){
        roseCouture.setRcNumber("orgId");
        assertThrows(MeedlException.class, () -> organizationIdentityService.updateOrganization(roseCouture));
    }
    @Test
    void updateOrganization() {
        try {
            roseCouture.setName(null);
            roseCouture.setRcNumber(null);
            when(organizationIdentityOutputPort.findById(anyString())).thenReturn(roseCouture);
            when(organizationIdentityMapper.updateOrganizationIdentity(roseCouture,roseCouture)).thenReturn(roseCouture);
            when(organizationIdentityOutputPort.save(roseCouture)).thenAnswer(invocation -> {
                roseCouture.setTimeUpdated(LocalDateTime.now());
                roseCouture.setUpdatedBy(mockId);
                roseCouture.setWebsiteAddress("newwebsite");
                return roseCouture;
            });
            roseCouture.setUpdatedBy(mockId);
            roseCouture.setId(mockId);
            OrganizationIdentity updateOrganization = organizationIdentityService.updateOrganization(roseCouture);
            assertNotNull(updateOrganization);
            assertNotNull(updateOrganization.getUpdatedBy());
            assertNotNull(updateOrganization.getTimeUpdated());
            assertEquals(roseCouture.getWebsiteAddress(), updateOrganization.getWebsiteAddress());
            assertEquals(roseCouture.getName(), updateOrganization.getName());
        } catch (Exception e) {
            log.error("Failed to update organization {}", e.getMessage());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void reactivateOrganizationWithInvalidId(String id) {
        assertThrows(MeedlException.class, () -> organizationIdentityService.reactivateOrganization(id, "test reason"));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void reactivateOrganizationWithEmptyReason(String reason) {
        assertThrows(MeedlException.class, () -> organizationIdentityService.reactivateOrganization(mockId, reason));
    }
    @Test
    void reactivateOrganization() {
        try {
            roseCouture.setEnabled(Boolean.TRUE);
            doNothing().when(identityManagerOutPutPort).enableClient(roseCouture);
            when(organizationIdentityOutputPort.findById(roseCouture.getId())).thenReturn(roseCouture);
            when(identityManagerOutPutPort.enableUserAccount(sarah)).thenReturn(sarah);
            when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
            OrganizationIdentity deactivatedOrganization =
                    organizationIdentityService.reactivateOrganization(roseCouture.getId(), "test 2 reason");
            assertTrue(deactivatedOrganization.isEnabled());
            assertEquals(ActivationStatus.ACTIVE, employeeSarah.getStatus());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void deactivateOrganizationWithInvalidId(String id) {
        assertThrows(MeedlException.class, () -> organizationIdentityService.deactivateOrganization(id, "test reason"));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void deactivateOrganizationWithEmptyReason(String reason) {
        assertThrows(MeedlException.class, () -> organizationIdentityService.deactivateOrganization(mockId, reason));
    }

    @Test
    void deactivateOrganization() {
        try {
            roseCouture.setEnabled(Boolean.FALSE);
            doNothing().when(identityManagerOutPutPort).disableClient(roseCouture);
            when(organizationIdentityOutputPort.findById(roseCouture.getId())).thenReturn(roseCouture);
            when(identityManagerOutPutPort.disableUserAccount(sarah)).thenReturn(sarah);
            when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
            OrganizationIdentity deactivatedOrganization =
                    organizationIdentityService.deactivateOrganization(roseCouture.getId(), "test 2 reason");
            assertFalse(deactivatedOrganization.isEnabled());
            assertEquals(ActivationStatus.DEACTIVATED, deactivatedOrganization.getStatus());
            assertEquals(ActivationStatus.DEACTIVATED, employeeSarah.getStatus());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void searchOrganizationWithInvalidName(String name) {
        Page<OrganizationIdentity> organizationIdentities = Page.empty();
        try{
            roseCouture.setName(name);
            organizationIdentities = organizationIdentityService.search(roseCouture);
        }catch (MeedlException e){
            log.info("{} {}", e.getClass().getName(), e.getMessage());
        }
        assertNull(organizationIdentities);
    }

    @Test
    void shouldReturnOrganizationDetails_WhenUserIsOrganizationAdmin() throws MeedlException {

        OrganizationLoanDetail loanDetail = OrganizationLoanDetail.builder()
                .amountRepaid(BigDecimal.valueOf(5000))
                .amountRequested(BigDecimal.valueOf(5000))
                .outstandingAmount(BigDecimal.valueOf(10000))
                .build();

        when(userIdentityOutputPort.findById(mockId)).thenReturn(sarah);
        sarah.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeSarah.setOrganization(roseCouture.getId());
        when(organizationEmployeeIdentityOutputPort.findByCreatedBy(mockId)).thenReturn(employeeSarah);
        when(organizationIdentityOutputPort.findById(roseCouture.getId())).thenReturn(roseCouture);

        when(organizationIdentityOutputPort.findById(roseCouture.getId())).thenReturn(roseCouture);
        when(organizationIdentityOutputPort.getServiceOfferings(roseCouture.getId())).thenReturn(roseCouture.getServiceOfferings());
        when(organizationLoanDetailOutputPort.findByOrganizationId(roseCouture.getId())).thenReturn(loanDetail);
        when(loanOfferOutputPort.countNumberOfPendingLoanOfferForOrganization(roseCouture.getId())).thenReturn(3);
        OrganizationIdentity result = organizationIdentityService.viewOrganizationDetails(null, mockId);

        assertNotNull(result);
        assertEquals(roseCouture.getId(), result.getId());
        assertEquals(roseCouture.getName(), result.getName());
        assertEquals(1, result.getOrganizationEmployees().size());
        assertEquals(1, result.getServiceOfferings().size());
        verify(userIdentityOutputPort).findById(mockId);
        verify(organizationIdentityOutputPort).findById(roseCouture.getId());
    }

    @Test
    void shouldReturnOrganizationDetails_WhenUserIsPortfolioManager() throws MeedlException {
        sarah.setRole(IdentityRole.PORTFOLIO_MANAGER);

        OrganizationLoanDetail loanDetail = OrganizationLoanDetail.builder()
                .amountRepaid(BigDecimal.valueOf(5000))
                .amountRequested(BigDecimal.valueOf(5000))
                .outstandingAmount(BigDecimal.valueOf(10000))
                .build();
        when(userIdentityOutputPort.findById(mockId)).thenReturn(sarah);
        when(organizationIdentityOutputPort.findById(roseCouture.getId())).thenReturn(roseCouture);
        when(organizationIdentityOutputPort.getServiceOfferings(roseCouture.getId())).thenReturn(roseCouture.getServiceOfferings());
        when(organizationLoanDetailOutputPort.findByOrganizationId(roseCouture.getId())).thenReturn(loanDetail);
        when(loanOfferOutputPort.countNumberOfPendingLoanOfferForOrganization(roseCouture.getId())).thenReturn(3);
        OrganizationIdentity result = organizationIdentityService.viewOrganizationDetails(roseCouture.getId(), mockId);
        assertNotNull(result);
        assertEquals(roseCouture.getId(), result.getId());
        assertEquals(3, result.getPendingLoanOfferCount());
        verify(userIdentityOutputPort).findById(mockId);
        verify(organizationLoanDetailOutputPort).findByOrganizationId(roseCouture.getId());
        verify(loanOfferOutputPort).countNumberOfPendingLoanOfferForOrganization(roseCouture.getId());
    }

    @Test
    void shouldThrowException_WhenOrganizationIdIsNullForPortfolioManager() {
        sarah.setRole(IdentityRole.PORTFOLIO_MANAGER);
        try {
            when(userIdentityOutputPort.findById(mockId)).thenReturn(sarah);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, () ->
                organizationIdentityService.viewOrganizationDetails(null, mockId)
        );
    }

}
