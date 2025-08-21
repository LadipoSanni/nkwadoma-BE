    package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.InstituteMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.OrganizationServiceOfferingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ServiceOfferingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
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
    private UserIdentity superAdmin;
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
    @Mock
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    @Mock
    private InstituteMetricsOutputPort instituteMetricsOutputPort;
    @Mock
    private ServiceOfferingOutputPort serviceOfferingOutputPort;
    @Mock
    private OrganizationServiceOfferingOutputPort organizationServiceOfferingOutputPort;

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

        superAdmin = TestData.createTestUserIdentity("superAdmin@grr.la");

    }

    @Test
    void inviteOrganization() {
        OrganizationIdentity invitedOrganisation = new OrganizationIdentity();
        try {
            superAdmin.setRole(IdentityRole.MEEDL_SUPER_ADMIN);
            roseCouture.setCreatedBy(superAdmin.getId());
            when(userIdentityOutputPort.findById(roseCouture.getCreatedBy())).thenReturn(superAdmin);
            when(identityManagerOutPutPort.createKeycloakClient(roseCouture)).thenReturn(roseCouture);
            when(identityManagerOutPutPort.createUser(sarah)).thenReturn(sarah);
            when(organizationIdentityOutputPort.save(roseCouture)).thenReturn(roseCouture);
            when(userIdentityOutputPort.save(sarah)).thenReturn(sarah);
            when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
            when(identityManagerOutPutPort.getClientRepresentationByName(roseCouture.getName())).thenReturn(new ClientRepresentation());
            when(identityManagerOutPutPort.getUserByEmail(roseCouture.getOrganizationEmployees().get(0).getMeedlUser().getEmail())).thenReturn(Optional.empty());
            doNothing().when(asynchronousMailingOutputPort).sendEmailToInvitedOrganization(any(UserIdentity.class));
//            when()
//            doNothing().when(asynchronousNotificationOutputPort).notifySuperAdminOfNewOrganization(any(UserIdentity.class),
//                    any(OrganizationIdentity.class), any(NotificationFlag.class));
            when(loanMetricsUseCase.createLoanMetrics(anyString())).thenReturn(new LoanMetrics());

            OrganizationLoanDetail organizationLoanDetail = TestData.buildOrganizationLoanDetail(roseCouture);
            when(organizationLoanDetailOutputPort.save(any())).thenReturn(organizationLoanDetail);
            invitedOrganisation = organizationIdentityService.inviteOrganization(roseCouture);
            assertNotNull(invitedOrganisation);
            assertNotNull(invitedOrganisation.getServiceOfferings());
            assertEquals(roseCouture.getName(), invitedOrganisation.getName());
            assertEquals(ActivationStatus.INVITED, employeeSarah.getActivationStatus());
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
        roseCouture2.setActivationStatus(ActivationStatus.ACTIVE);

        int pageNumber = 0;
        int pageSize = 10;
        roseCouture.setPageNumber(pageNumber);
        roseCouture.setPageSize(pageSize);
        roseCouture.setActivationStatus(ActivationStatus.ACTIVE);

        List<OrganizationIdentity> organizationIdentities = new ArrayList<>();
        organizationIdentities.add(roseCouture);
        organizationIdentities.add(roseCouture2);

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "invitedDate"));
        Page<OrganizationIdentity> organizationIdentityPage = new PageImpl<>(organizationIdentities, pageRequest, organizationIdentities.size());

        when(organizationIdentityOutputPort.viewAllOrganizationByStatus(roseCouture, List.of(ActivationStatus.ACTIVE.name()))).thenReturn(organizationIdentityPage);
        Page<OrganizationIdentity> result = organizationIdentityService.viewAllOrganizationByStatus(roseCouture, ActivationStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(ActivationStatus.ACTIVE, result.getContent().get(1).getActivationStatus());
        verify(organizationIdentityOutputPort, times(1)).viewAllOrganizationByStatus(roseCouture, List.of(ActivationStatus.ACTIVE.name()));
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
            assertEquals(ActivationStatus.ACTIVE, employeeSarah.getActivationStatus());
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
            assertEquals(ActivationStatus.DEACTIVATED, deactivatedOrganization.getActivationStatus());
            assertEquals(ActivationStatus.DEACTIVATED, employeeSarah.getActivationStatus());
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
    void viewOrganizationDetailByAdmin() throws MeedlException {

        OrganizationLoanDetail loanDetail = OrganizationLoanDetail.builder()
                .amountRepaid(BigDecimal.valueOf(5000))
                .amountRequested(BigDecimal.valueOf(5000))
                .outstandingAmount(BigDecimal.valueOf(10000))
                .build();

        when(userIdentityOutputPort.findById(mockId)).thenReturn(sarah);
        when(userIdentityOutputPort.findById(roseCouture.getCreatedBy())).thenReturn(sarah);
        sarah.setRole(IdentityRole.ORGANIZATION_ADMIN);
        employeeSarah.setOrganization(roseCouture.getId());
        when(organizationEmployeeIdentityOutputPort.findByCreatedBy(mockId)).thenReturn(employeeSarah);


        when(organizationIdentityOutputPort.findByIdProjection(roseCouture.getId())).thenReturn(roseCouture);
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
        verify(organizationIdentityOutputPort).findByIdProjection(roseCouture.getId());
    }

    @Test
    void viewOrganizationDetailByPortfolioManager() throws MeedlException {
        sarah.setRole(IdentityRole.PORTFOLIO_MANAGER);

        OrganizationLoanDetail loanDetail = OrganizationLoanDetail.builder()
                .amountRepaid(BigDecimal.valueOf(5000))
                .amountRequested(BigDecimal.valueOf(5000))
                .outstandingAmount(BigDecimal.valueOf(10000))
                .build();
        when(userIdentityOutputPort.findById(mockId)).thenReturn(sarah);
        when(userIdentityOutputPort.findById(roseCouture.getCreatedBy())).thenReturn(sarah);
        when(organizationIdentityOutputPort.findByIdProjection(roseCouture.getId())).thenReturn(roseCouture);
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
    void approveOrganizationInvitationWithInValidResponse(){
        assertThrows(MeedlException.class, () ->
                organizationIdentityService.respondToOrganizationInvite(mockId,mockId,ActivationStatus.ACTIVE));
    }

    @Test
    void approveOrganizationInvitationWithNullResponse(){
        assertThrows(MeedlException.class, () ->
                organizationIdentityService.respondToOrganizationInvite(mockId,mockId,null));
    }

    @Test
    void approveOrganizationInvitationWithNullOrganizationId(){
        assertThrows(MeedlException.class, () ->
                organizationIdentityService.respondToOrganizationInvite(mockId,null,ActivationStatus.ACTIVE));
    }

    @Test
    void cannotApproveInvitationForActivatedOrganization() throws MeedlException {
        when(userIdentityOutputPort.findById(anyString())).thenReturn(superAdmin);
        when(organizationIdentityOutputPort.findById(anyString())).thenReturn(roseCouture);
        when(userIdentityOutputPort.findById(roseCouture.getCreatedBy())).thenReturn(sarah);
        roseCouture.setActivationStatus(ActivationStatus.ACTIVE);
        assertThrows(MeedlException.class, () ->
                organizationIdentityService.respondToOrganizationInvite(mockId,mockId,ActivationStatus.APPROVED));
    }

    @Test
    void approveOrganizationInvitation() throws MeedlException {
        when(userIdentityOutputPort.findById(anyString())).thenReturn(superAdmin);
        when(organizationIdentityOutputPort.findById(anyString())).thenReturn(roseCouture);
        when(userIdentityOutputPort.findById(roseCouture.getCreatedBy())).thenReturn(sarah);
        roseCouture.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
        doNothing().when(asynchronousMailingOutputPort).sendEmailToInvitedOrganization(any(UserIdentity.class));
        when(meedlNotificationOutputPort.save(any(MeedlNotification.class))).thenReturn(any(MeedlNotification.class));
        String response = organizationIdentityService.respondToOrganizationInvite(mockId,mockId,ActivationStatus.APPROVED);
        assertNotNull(response);
    }


    @Test
    void inviteColleague() {
        String response = "";
        roseCouture.setUserIdentity(sarah);
        try {
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(sarah.getCreatedBy())).thenReturn(employeeSarah);
            when(identityManagerOutPutPort.createUser(sarah)).thenReturn(sarah);
            when(userIdentityOutputPort.save(sarah)).thenReturn(sarah);

            OrganizationEmployeeIdentity savedEmployee = new OrganizationEmployeeIdentity();
            savedEmployee.setMeedlUser(sarah);
            savedEmployee.setOrganization(employeeSarah.getOrganization());
            savedEmployee.setActivationStatus(ActivationStatus.PENDING_APPROVAL);

            when(organizationEmployeeIdentityOutputPort.save(any())).thenReturn(savedEmployee);

            MeedlNotification notification = MeedlNotification.builder()
                    .title("Pending colleague invitation")
                    .contentDetail("Need Approval for colleague invitation")
                    .senderFullName(employeeSarah.getMeedlUser().getFirstName() + " " + employeeSarah.getMeedlUser().getLastName())
                    .notificationFlag(NotificationFlag.INVITE_COLLEAGUE)
                    .timestamp(LocalDateTime.now())
                    .contentId(savedEmployee.getId())
                    .callToAction(true)
                    .user(employeeSarah.getMeedlUser())
                    .build();

            OrganizationEmployeeIdentity superAdmin = new OrganizationEmployeeIdentity();
            superAdmin.setMeedlUser(new UserIdentity());
            employeeSarah.setOrganization(mockId);
            when(organizationEmployeeIdentityOutputPort.findByRoleAndOrganizationId(
                    eq(employeeSarah.getOrganization()), eq(IdentityRole.MEEDL_SUPER_ADMIN)))
                    .thenReturn(superAdmin);


            response = organizationIdentityService.inviteColleague(roseCouture);

        }catch (MeedlException meedlException){
            log.info(meedlException.getMessage());
        }
        assertNotNull(response);
        assertEquals("Invitation needs approval, pending.", response);
    }

    @Test
    void inviteColleagueWithNullInviterId(){
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEmail("linda@grr.la");
        userIdentity.setFirstName("first name");
        userIdentity.setLastName("last name");
        userIdentity.setCreatedBy(null);
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        assertThrows(MeedlException.class,()-> organizationIdentityService.inviteColleague(organizationIdentity));
    }
    @Test
    void  inviteColleagueWithNullUserIdentity(){
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setUserIdentity(null);
        assertThrows(MeedlException.class,()-> organizationIdentityService.inviteColleague(organizationIdentity));
    }
    @Test
    void  inviteColleagueWitNullEmail(){
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEmail(null);
        userIdentity.setFirstName("first name");
        userIdentity.setLastName("last name");
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        assertThrows(MeedlException.class,()-> organizationIdentityService.inviteColleague(organizationIdentity));
    }

    @Test
    void  inviteColleagueWitNullFirstName(){
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEmail("linda@grr.la");
        userIdentity.setFirstName(null);
        userIdentity.setLastName("last name");
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        assertThrows(MeedlException.class,()-> organizationIdentityService.inviteColleague(organizationIdentity));
    }

    @Test
    void  inviteColleagueWitNullLastName(){
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setEmail("linda@grr.la");
        userIdentity.setFirstName("last name");
        userIdentity.setLastName(null);
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        assertThrows(MeedlException.class,()-> organizationIdentityService.inviteColleague(organizationIdentity));
    }


}
