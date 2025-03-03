package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
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
    private SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    @Mock
    private LoanMetricsUseCase loanMetricsUseCase;
    private UserIdentity sarah;
    private OrganizationIdentity roseCouture;
    private OrganizationEmployeeIdentity employeeSarah;
    private List<OrganizationEmployeeIdentity> orgEmployee;
    private final String mockId = "83f744df-78a2-4db6-bb04-b81545e78e49";

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
            doNothing().when(sendOrganizationEmployeeEmailUseCase).sendEmail(sarah);
            when(loanMetricsUseCase.createLoanMetrics(anyString())).thenReturn(new LoanMetrics());

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
    void viewAllOrganizationsLoanRequests() {
        when(organizationIdentityOutputPort.findAllWithLoanMetrics()).thenReturn(List.of(roseCouture));
        List<OrganizationIdentity> organizationIdentities = organizationIdentityService.viewAllOrganizationsLoanMetrics();
        verify(organizationIdentityOutputPort, times(1)).findAllWithLoanMetrics();
        assertNotNull(organizationIdentities);
        assertEquals(organizationIdentities.get(0).getId(), roseCouture.getId());
        assertEquals(organizationIdentities.get(0).getName(), roseCouture.getName());
        assertEquals(organizationIdentities.get(0).getLogoImage(), roseCouture.getLogoImage());
    }

    @Test
    void viewAllOrganizationWithStatusTakingNullParameter() {
        assertThrows(MeedlException.class, ()-> organizationIdentityService.viewAllOrganizationByStatus(roseCouture, null));
    }

    @Test
    void viewAllOrganizationWithStatus() throws MeedlException {

        OrganizationIdentity roseCouture2 = new OrganizationIdentity();
        roseCouture2.setId("83f744df-78a2-4db6-bb04-b81545e78e49");
        roseCouture2.setName("rose couture6");
        roseCouture2.setEmail("iamoluchimercy@gmail.com");
        roseCouture2.setTin("7682-5627");
        roseCouture2.setRcNumber("RC8789905");
        roseCouture2.setServiceOfferings(List.of(new ServiceOffering()));
        roseCouture.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        roseCouture2.setPhoneNumber("09876365714");
        roseCouture2.setInvitedDate(LocalDateTime.now().toString());
        roseCouture2.setWebsiteAddress("rosecouture2.org");
        roseCouture2.setOrganizationEmployees(orgEmployee);
        roseCouture2.setEnabled(Boolean.TRUE);
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

        // Assert
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
            when(organizationEntityRepository.save(organizationIdentityMapper.toOrganizationEntity(roseCouture))).
                    thenReturn(any());
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
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void searchOrganizationWithInvalidName(String name) {
        assertThrows(MeedlException.class, ()->organizationIdentityService.search(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "nfkjdnjnf"})
    void viewOrganizationWithInvalidId(String id) {
        assertThrows(MeedlException.class, ()->organizationIdentityService.viewOrganizationDetails(id));
    }
}
