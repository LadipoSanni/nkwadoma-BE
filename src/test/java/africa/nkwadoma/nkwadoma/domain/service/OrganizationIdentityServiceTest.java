package africa.nkwadoma.nkwadoma.domain.service;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.UserIdentityAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@SpringBootTest
//@ExtendWith(MockitoExtension.class)
class OrganizationIdentityServiceTest {
//    @InjectMocks
    @Autowired
    private OrganizationIdentityService organizationIdentityService;
//    @Mock
//    private OrganizationIdentityAdapter organizationIdentityAdapter;
//    @Mock
//    private KeycloakAdapter keycloakAdapter;


    @Autowired
    private OrganizationIdentityAdapter organizationAdapter;
    @Autowired
    private KeycloakAdapter keycloakAutowiredAdapter;

    private OrganizationIdentity roseCouture;

    @BeforeEach
        void setUp(){

            UserIdentity sarah = new UserIdentity();
            sarah.setRole(IdentityRole.PORTFOLIO_MANAGER.toString());
            sarah.setFirstName("Sarah");
            sarah.setLastName("Jacobs");
            sarah.setEmail("sarah6@gmail.com");
            sarah.setCreatedBy("joseph");

            OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity();
            employeeIdentity.setMiddlUser(sarah);

            List<OrganizationEmployeeIdentity> orgEmployee = new ArrayList<>();
            orgEmployee.add(employeeIdentity);


            roseCouture = new OrganizationIdentity();
            roseCouture.setName("rose couture6");
            roseCouture.setEmail("rosecouture@gmail.com");
            roseCouture.setTin("7682-5627");
            roseCouture.setRcNumber("RC87899");
            roseCouture.setIndustry("education");
            roseCouture.setPhoneNumber("09876365713");
            roseCouture.setInvitedDate(LocalDateTime.now().toString());
            roseCouture.setWebsiteAddress("rosecouture.org");
            roseCouture.setOrganizationEmployees(orgEmployee);

    }
//
//    @Test
//    void inviteOrganization(){
//        try{
//            doNothing().when(keycloakAdapter).inviteOrganization(roseCouture);
//            organizationIdentityService.inviteOrganization(roseCouture);
//            verify(keycloakAdapter, times(1)).inviteOrganization(roseCouture);
//        }catch (MiddlException exception){
//            log.info("{} {}",exception.getClass().getName(), exception.getMessage());
//        }
//    }

    @Test
    void inviteOrganizationForReal() {
        try {
            assertThrows(MiddlException.class, () -> organizationAdapter.findById(roseCouture.getId()));
            organizationIdentityService.inviteOrganization(roseCouture);

            OrganizationIdentity foundOrganization = organizationAdapter.findById(roseCouture.getId());
            assertEquals(roseCouture.getName(), foundOrganization.getName());
           // assertEquals(roseCouture.getOrganizationAdmins().size(), foundOrganization.getOrganizationAdmins().size());
        } catch (MiddlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }



    @Test
    void inviteOrganizationWithEmptyOrganization(){
        assertThrows(MiddlException.class, () -> organizationIdentityService.inviteOrganization(new OrganizationIdentity()));
    }




}