package africa.nkwadoma.nkwadoma.domain.service.identity;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.ViewOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@Slf4j
@ExtendWith(MockitoExtension.class)
class OrganizationIdentityServiceTest {

    @InjectMocks
    private OrganizationIdentityService organizationIdentityService;

    @Mock
    private IdentityManagerOutPutPort identityManagerOutPutPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock

    private SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;

    private OrganizationIdentity roseCouture;
    private UserIdentity sarah;
    private OrganizationEmployeeIdentity employeeSarah ;
    private List<OrganizationEmployeeIdentity> orgEmployee;

    @BeforeEach
    void setUp(){

        sarah = new UserIdentity();
        sarah.setRole(IdentityRole.PORTFOLIO_MANAGER);
        sarah.setId("83f744df-78a2-4db6-bb04-b81545e78e49");
        sarah.setFirstName("Sarah");
        sarah.setLastName("Jacobs");
        sarah.setEmail("divinemercy601@gmail.com");
        sarah.setCreatedBy("83f744df-78a2-4db6-bb04-b81545e78e49");

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
        roseCouture.setRcNumber("RC87899");
        roseCouture.setServiceOfferings(List.of(new ServiceOffering()));
        roseCouture.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        roseCouture.setPhoneNumber("09876365713");
        roseCouture.setInvitedDate(LocalDateTime.now().toString());
        roseCouture.setWebsiteAddress("rosecouture.org");
        roseCouture.setOrganizationEmployees(orgEmployee);
//        roseCouture.setEnabled(Boolean.TRUE);

    }

    @Test
    void inviteOrganization() {
        OrganizationIdentity invitedOrganisation = null;
        try {
            when(identityManagerOutPutPort.createOrganization(roseCouture)).thenReturn(roseCouture);
            when(identityManagerOutPutPort.createUser(sarah)).thenReturn(sarah);
            when(organizationIdentityOutputPort.save(roseCouture)).thenReturn(roseCouture);
            when(userIdentityOutputPort.save(sarah)).thenReturn(sarah);
            when(organizationEmployeeIdentityOutputPort.save(employeeSarah)).thenReturn(employeeSarah);
            doNothing().when(sendOrganizationEmployeeEmailUseCase).sendEmail(sarah);

            invitedOrganisation = organizationIdentityService.inviteOrganization(roseCouture);
            assertNotNull(invitedOrganisation);
            assertEquals(roseCouture.getName(), invitedOrganisation.getName());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }
    @Test
    void inviteOrganizationWithEmptyOrganization(){
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(new OrganizationIdentity()));
    }
    @Test
    void inviteOrganizationWithNullOrganization(){
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(null));
    }
    @Test
    void inviteOrganizationWithNullEmail(){
        roseCouture.setEmail(null);
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(roseCouture));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "fndnkfjdf"})
    void inviteOrganizationWithInvalidEmail(String email){
        roseCouture.setEmail(email);
        assertThrows(MeedlException.class, () -> organizationIdentityService.inviteOrganization(roseCouture));
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



