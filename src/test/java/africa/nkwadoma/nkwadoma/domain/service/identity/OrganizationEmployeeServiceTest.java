package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
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

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganizationEmployeeServiceTest {
    @InjectMocks
    private OrganizationEmployeeService organizationEmployeeService;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeOutputPort;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;

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
        organizationEmployeeIdentity.setMiddlUser(userIdentity);
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
}