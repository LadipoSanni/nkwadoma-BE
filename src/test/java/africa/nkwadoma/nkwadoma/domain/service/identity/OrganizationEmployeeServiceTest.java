package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
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
    private int pageNumber = 0;
    private int pageSize = 10;
    private String mockId = "5756faf2-f3c8-40c4-9af5-5946adcfebd9";

    @BeforeEach
    void setUp() {
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setFirstName("John");
        userIdentity.setLastName("Doe");
        userIdentity.setEmail("john@example.com");
        organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization("5756faf2-f3c8-40c4-9af5-5946adcfebd9");
        organizationEmployeeIdentity.setPageNumber(0);
        organizationEmployeeIdentity.setPageSize(10);
        organizationEmployeeIdentity.setMeedlUser(userIdentity);
        organizationEmployeeIdentity.setId("79a44827-ba7a-4d42-be8a-62b357ac4148");
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
        String userId = UUID.randomUUID().toString();
        UserIdentity user = new UserIdentity();
        user.setId(userId);
        user.setRole(IdentityRole.MEEDL_ADMIN);

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);
        request.setPageSize(10);
        request.setPageNumber(0);

        OrganizationIdentity organization = new OrganizationIdentity();
        organization.setId("org-123");

        when(userIdentityOutputPort.findById(userId)).thenReturn(user);
        when(organizationIdentityOutputPort.findByUserId(userId)).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.findAllAdminInOrganization(eq("org-123"), any())).thenReturn(Page.empty());

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(request);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(request.getIdentityRoles().contains(IdentityRole.MEEDL_ADMIN));
    }

    @Test
    void viewAllAdminInOrganizationWithExplicitRolesProvided() throws MeedlException {
        String userId = UUID.randomUUID().toString();
        UserIdentity user = new UserIdentity();
        user.setId(userId);
        user.setRole(IdentityRole.ORGANIZATION_ADMIN);

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);
        request.setPageSize(10);
        request.setPageNumber(0);
        request.setIdentityRoles(Set.of(IdentityRole.ORGANIZATION_ADMIN));

        OrganizationIdentity organization = new OrganizationIdentity();
        organization.setId("org-456");


        when(userIdentityOutputPort.findById(userId)).thenReturn(user);
        when(organizationIdentityOutputPort.findByUserId(userId)).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.findAllAdminInOrganization(eq("org-456"), any())).thenReturn(Page.empty());

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(request);
        assertNotNull(result);
        assertEquals(Set.of(IdentityRole.ORGANIZATION_ADMIN), request.getIdentityRoles());
    }

    @Test
    void viewAllAdminInOrganizationWhenUserIsNotInOrganization() throws MeedlException {
        String userId = UUID.randomUUID().toString();
        UserIdentity user = new UserIdentity();
        user.setId(userId);
        user.setRole(IdentityRole.ORGANIZATION_ADMIN);

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);

        when(userIdentityOutputPort.findById(userId)).thenReturn(user);
        when(organizationIdentityOutputPort.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(MeedlException.class, () -> organizationEmployeeService.viewAllAdminInOrganization(request));
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
    void viewAllAdminInOrganizationWithOrgAdminViewingPortfolioRoleDoesNotSetMeedlRoles() throws MeedlException {
        String userId = UUID.randomUUID().toString();
        UserIdentity user = new UserIdentity();
        user.setId(userId);
        user.setRole(IdentityRole.ORGANIZATION_ADMIN);

        OrganizationEmployeeIdentity request = new OrganizationEmployeeIdentity();
        request.setMeedlUser(user);
        request.setPageSize(10);
        request.setPageNumber(0);
        request.setIdentityRoles(Set.of(IdentityRole.PORTFOLIO_MANAGER));

        OrganizationIdentity organization = new OrganizationIdentity();
        organization.setId("org-789");

        when(userIdentityOutputPort.findById(userId)).thenReturn(user);
        when(organizationIdentityOutputPort.findByUserId(userId)).thenReturn(Optional.of(organization));
        when(organizationEmployeeOutputPort.findAllAdminInOrganization(eq("org-789"), any())).thenReturn(Page.empty());

        Page<OrganizationEmployeeIdentity> result = organizationEmployeeService.viewAllAdminInOrganization(request);
        assertNotNull(result);
        assertTrue(request.getIdentityRoles().contains(IdentityRole.PORTFOLIO_MANAGER)); // remains as provided
    }

}