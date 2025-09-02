package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganizationEmployeeServiceTest {
    @InjectMocks
    private OrganizationEmployeeService organizationEmployeeService;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeOutputPort;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final String mockId = UUID.randomUUID().toString();
    private UserIdentity userIdentity;
    private OrganizationIdentity organization ;
    @Mock
    private AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    @Mock
    private AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;


    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("John");
        userIdentity.setLastName("Doe");
        userIdentity.setId(mockId);
        userIdentity.setRole(IdentityRole.MEEDL_SUPER_ADMIN);
        userIdentity.setEmail("john@example.com");
        organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(mockId);
        organizationEmployeeIdentity.setPageNumber(0);
        organizationEmployeeIdentity.setPageSize(10);
        organizationEmployeeIdentity.setMeedlUser(userIdentity);
        organizationEmployeeIdentity.setId(mockId);

        organization = new OrganizationIdentity();
        organization.setId(UUID.randomUUID().toString());

    }

    @Test
    void viewOrganizationEmployees() {
        try {
            when(organizationEmployeeOutputPort.findAllOrganizationEmployees(organizationEmployeeIdentity.getOrganization(),
                    organizationEmployeeIdentity.getPageNumber(), organizationEmployeeIdentity.getPageSize()))
                    .thenReturn(new PageImpl<>(List.of(new OrganizationEmployeeIdentity())));
            Page<OrganizationEmployeeIdentity> employeeIdentities = organizationEmployeeService.
                    viewOrganizationEmployees(organizationEmployeeIdentity);
            assertNotNull(employeeIdentities);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void viewAllOrganizationEmployeesWithNonExistingOrganizationId() {
        organizationEmployeeIdentity.setOrganization("3a6d1124-1349-4f5b-831a-ac269369a90f");
        organizationEmployeeIdentity.setPageSize(pageSize);
        organizationEmployeeIdentity.setPageNumber(pageNumber);
        assertThrows(MeedlException.class, () -> organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }

    @ParameterizedTest
    @ValueSource(strings = {"03945988", "non-uuid"})
    void viewAllOrganizationEmployeesWithNonUUIDId(String organizationId) {
        organizationEmployeeIdentity.setOrganization(organizationId);
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewAllOrganizationEmployeesWithNullId(String organizationId) {
        organizationEmployeeIdentity.setOrganization(organizationId);
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }


    @ParameterizedTest
    @ValueSource(ints = {-1})
    void viewAllOrganizationEmployeesWithInvalidPageNumber(int pageNumber) {
        organizationEmployeeIdentity.setPageNumber(pageNumber);
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1})
    void viewAllOrganizationEmployeesWithInvalidPageSize(int pageSize) {
        organizationEmployeeIdentity.setPageNumber(pageSize);
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }
    
    @Test
    void viewAllOrganizationEmployeesWithNullEmployeeIdentity() {
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1b5daeb6-fca3-47b3-9fea-de9d83826239"})
    void viewAllOrganizationEmployeesWithNonExistingOrganizationId(String organizationId) {
        organizationEmployeeIdentity.setOrganization(organizationId);
        try {
            when(organizationEmployeeOutputPort.findAllOrganizationEmployees(organizationEmployeeIdentity.getOrganization(),
                    organizationEmployeeIdentity.getPageNumber(), organizationEmployeeIdentity.getPageSize())).thenThrow(IdentityException.class);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertThrows(MeedlException.class, ()->organizationEmployeeService.
                viewOrganizationEmployees(organizationEmployeeIdentity));
    }

    @Test
    void searchOrganizationEmployees() {
        Page<OrganizationEmployeeIdentity> employeeIdentities = new
                PageImpl<>(List.of(new OrganizationEmployeeIdentity()));
        try {
            when(organizationIdentityOutputPort.findById(mockId))
                    .thenReturn(new OrganizationIdentity());
            when(organizationEmployeeOutputPort.findAllEmployeesInOrganization(mockId,"j",pageSize,pageNumber))
                    .thenReturn(employeeIdentities);
            employeeIdentities = organizationEmployeeService.searchAdminInOrganization(mockId,"j",pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertEquals(employeeIdentities.getContent().size(),1);
    }
    @Test
    void viewAllAdminInOrganizationWithValidMeedlStaffRole() throws MeedlException {

        OrganizationIdentity organization = new OrganizationIdentity();
        organization.setId("org-123");

        when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
        when(organizationIdentityOutputPort.findByUserId(userIdentity.getId())).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.searchOrFindAllAdminInOrganization(eq("org-123"), any())).thenReturn(Page.empty());

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(organizationEmployeeIdentity);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(organizationEmployeeIdentity.getIdentityRoles().contains(IdentityRole.MEEDL_ADMIN));
    }

    @Test
    void viewAllAdminInOrganizationWhenUserIsNotInOrganization() throws MeedlException {

        when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
        when(organizationIdentityOutputPort.findByUserId(userIdentity.getId())).thenReturn(Optional.empty());

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(organizationEmployeeIdentity));
    }

    @Test
    void viewAllAdminInOrganizationWhenUserIdIsInvalid() {
        UserIdentity user = new UserIdentity();
        user.setId("invalid-uuid");

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(request));
    }

    @Test
    void viewAllAdminInOrganizationWhenMeedlUserIsNull() {
        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(null);

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(request));
    }


    @Test
    void setsMeedlRolesForPendingApprovalForMeedlSuperAdmin() {
        userIdentity.setRole(IdentityRole.MEEDL_SUPER_ADMIN);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(IdentityRole.getMeedlRoles(), organizationEmployeeIdentity.getIdentityRoles());
    }

    @Test
    void setsPortfolioManagerAndAssociateRolesForPendingApprovalByPortfolioManager() {
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(Set.of(IdentityRole.PORTFOLIO_MANAGER, IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE),
                organizationEmployeeIdentity.getIdentityRoles());
    }

    @Test
    void setsOrganizationRolesForPendingApprovalByOrganizationSuperAdmin_() {
        userIdentity.setRole(IdentityRole.ORGANIZATION_SUPER_ADMIN);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(IdentityRole.getOrganizationRoles(), organizationEmployeeIdentity.getIdentityRoles());
    }

    @Test
    void setsMeedlRolesForNonPendingApprovalByMeedlStaff() {
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(IdentityRole.getMeedlRoles(), organizationEmployeeIdentity.getIdentityRoles());
    }

    @Test
    void setsOrganizationRolesForNonPendingApprovalByOrganizationStaff() {
        userIdentity.setRole(IdentityRole.ORGANIZATION_ASSOCIATE);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(IdentityRole.getOrganizationRoles(), organizationEmployeeIdentity.getIdentityRoles());
    }


    @Test
    void pendingApprovalWithRolesProvidedDoesNotOverride() {
        userIdentity.setRole(IdentityRole.MEEDL_SUPER_ADMIN);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        organizationEmployeeIdentity.setIdentityRoles(Set.of(IdentityRole.MEEDL_ADMIN));

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(Set.of(IdentityRole.MEEDL_ADMIN), organizationEmployeeIdentity.getIdentityRoles());
    }

    @Test
    void nonPendingApprovalWithRolesProvidedDoesNotOverride() {
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        organizationEmployeeIdentity.setIdentityRoles(Set.of(IdentityRole.MEEDL_ADMIN));

        organizationEmployeeService.setRolesToView(organizationEmployeeIdentity, userIdentity);

        assertEquals(Set.of(IdentityRole.MEEDL_ADMIN), organizationEmployeeIdentity.getIdentityRoles());
    }



    @Test
    void searchOrganizationAdminWithValidRequestAndNoExplicitRoles() throws MeedlException {

        Page<OrganizationEmployeeIdentity> mockPage = new PageImpl<>(List.of(new OrganizationEmployeeIdentity()));
        organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getMeedlRoles());
        organizationEmployeeIdentity.setName(userIdentity.getEmail());
        when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
        when(organizationIdentityOutputPort.findByUserId(userIdentity.getId())).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.searchOrFindAllAdminInOrganization(eq(organization.getId()), any())).thenReturn(mockPage);

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(organizationEmployeeIdentity);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchOrganizationAdminWithExplicitRolesProvided() throws MeedlException {
        String userId = UUID.randomUUID().toString();
        UserIdentity user = new UserIdentity();
        user.setId(userId);
        user.setFirstName("User test");
        user.setRole(IdentityRole.ORGANIZATION_SUPER_ADMIN);

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);
        request.setIdentityRoles(Set.of(IdentityRole.ORGANIZATION_ADMIN));
        request.setName(user.getFirstName());

        when(userIdentityOutputPort.findById(userId)).thenReturn(user);
        when(organizationIdentityOutputPort.findByUserId(userId)).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.searchOrFindAllAdminInOrganization(eq(organization.getId()), any())).thenReturn(Page.empty());

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(request);
        assertNotNull(result);
        assertEquals(Set.of(IdentityRole.ORGANIZATION_ADMIN), request.getIdentityRoles());
    }

    @Test
    void searchOrganizationAdminWhenUserIdIsInvalidUUID() {
        UserIdentity user = new UserIdentity();
        user.setId("invalid-uuid");
        user.setFirstName("First test name");

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);
        request.setName(user.getFirstName());

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(request));
    }

    @Test
    void searchOrganizationAdminWhenMeedlUserIsNull() {
        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(null);

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(request));
    }
    @Test
    void searchOrganizationAdminWhenUserNotInOrganization() throws MeedlException {

        when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
        when(organizationIdentityOutputPort.findByUserId(userIdentity.getId())).thenReturn(Optional.empty());

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(organizationEmployeeIdentity));
    }


    @Test
    void approveColleagueInvitation(){
        String response = "";
        try {
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
            when(organizationEmployeeOutputPort.findById(any())).thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(any())).thenReturn(organization);
            when(organizationEmployeeOutputPort.save(organizationEmployeeIdentity)).thenReturn(organizationEmployeeIdentity);
            organizationEmployeeIdentity = organizationEmployeeService.respondToColleagueInvitation(mockId,mockId,ActivationStatus.APPROVED);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(response);
        assertEquals(organizationEmployeeIdentity.getResponse(),"Colleague invitation APPROVED for "+organizationEmployeeIdentity.getMeedlUser().getFullName());
    }

    @Test
    void declineColleagueInvitation(){
        String response = "";
        try {
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
            when(organizationEmployeeOutputPort.findById(any())).thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(any())).thenReturn(organization);
            when(organizationEmployeeOutputPort.save(organizationEmployeeIdentity)).thenReturn(organizationEmployeeIdentity);
            when(userIdentityOutputPort.findById(any())).thenReturn(userIdentity);

            organizationEmployeeIdentity = organizationEmployeeService.respondToColleagueInvitation(mockId,mockId,ActivationStatus.DECLINED);
            response = organizationEmployeeIdentity.getResponse()
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(response);
        assertEquals(response,"Colleague invitation DECLINED for "+organizationEmployeeIdentity.getMeedlUser().getFullName());
    }


    @Test
    void cannotRespondToActiveColleagueInvitation() throws MeedlException {
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        when(organizationEmployeeOutputPort.findById(any())).thenReturn(organizationEmployeeIdentity);
        assertThrows(MeedlException.class , () ->organizationEmployeeService.respondToColleagueInvitation(mockId,mockId,ActivationStatus.APPROVED));
    }

    @Test
    void respondToColleagueInvitationWithNullOrganizationEmployeeId(){
            assertThrows(MeedlException.class , () ->organizationEmployeeService.respondToColleagueInvitation(mockId,null,ActivationStatus.APPROVED));
    }



    @Test
    void viewEmployeeDetailWithValidId() throws MeedlException {
        organizationEmployeeIdentity.setId(mockId);
        OrganizationEmployeeIdentity expectedEmployee = new OrganizationEmployeeIdentity();
        expectedEmployee.setId(mockId);

        when(organizationEmployeeOutputPort.findById(mockId))
                .thenReturn(expectedEmployee);

        OrganizationEmployeeIdentity result =
                organizationEmployeeService.viewEmployeeDetail(organizationEmployeeIdentity);

        assertNotNull(result);
        assertEquals(mockId, result.getId());
        Mockito.verify(organizationEmployeeOutputPort, Mockito.times(1))
                .findById(mockId);
    }

    @Test
    void viewEmployeeDetailWithInvalidUUID() {
        organizationEmployeeIdentity.setId("not-a-uuid");

        assertThrows(MeedlException.class,
                () -> organizationEmployeeService.viewEmployeeDetail(organizationEmployeeIdentity));

        Mockito.verifyNoInteractions(organizationEmployeeOutputPort);
    }

    @Test
    void viewEmployeeDetailWithNullOrganizationEmployeeIdentity() {
        assertThrows(MeedlException.class,
                () -> organizationEmployeeService.viewEmployeeDetail(null));

        Mockito.verifyNoInteractions(organizationEmployeeOutputPort);
    }

}