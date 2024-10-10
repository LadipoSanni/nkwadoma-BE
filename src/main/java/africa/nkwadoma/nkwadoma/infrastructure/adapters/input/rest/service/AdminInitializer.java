package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//@AllArgsConstructor
public class AdminInitializer {

//    private final IdentityManagerOutPutPort identityManagerOutPutPort;
//    private final CreateUserUseCase createUserUseCase;

//    private final UserManager userManager;
//
//    @Value("${keycloak.client}")
//   private String CLIENT;

//    @Value("${superAdmin.email}")
    private String SUPER_ADMIN_EMAIL;

//    @Value("${superAdmin.password}")
    private String SUPER_ADMIN_PASSWORD;

//    @Value("${superAdmin.firstName}")
    private String SUPER_ADMIN_FIRST_NAME;

//    @Value("${superAdmin.lastName}")
    private String SUPER_ADMIN_LAST_NAME;

    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .firstName(SUPER_ADMIN_FIRST_NAME)
                .lastName(SUPER_ADMIN_LAST_NAME)
                .role(PORTFOLIO_MANAGER.name())
                .build();
    }
    private OrganizationIdentity getOrganizationIdentity() {

        return OrganizationIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .name("Middl")
                .phoneNumber("nil")
                .industry("Middl")
                .rcNumber("nil")
                .build();
    }
    @PostConstruct
    public void init(){
        //create user
//        UserIdentity userIdentity = getUserIdentity();
//        identityManagerOutPutPort.createUser(userIdentity);
//
//        //create organization
//        OrganizationIdentity organizationIdentity = getOrganizationIdentity();
//        identityManagerOutPutPort.createOrganization(organizationIdentity);
//
//        //invite colleague
//        createUserUseCase.inviteColleague(userIdentity);

//        createClientAndRoles(userIdentity);
//        saveUserToDatabase(request);
    }
//    private void createClientAndRoles(UserIdentity request)  {
//        List<ClientRepresentation> clientRepresentations = keyCloakUserService.getClients(CLIENT);
//        PortfolioManagerRequest portfolioManagerRequest = new PortfolioManagerRequest();
//        portfolioManagerRequest.setInstituteName(CLIENT);
//
//        if (clientRepresentations.isEmpty()) {
//            keyCloakUserService.createClient(portfolioManagerRequest);
//            keyCloakUserService.addRolesToRealm();
//
//        }

    }
//
//    private void saveUserToDatabase(UserRegistrationRequest request) throws LearnSpaceUserException {
//        List<UserRepresentation> users = keyCloakUserService.getUserRepresentations(request.getEmail());
//        if(!userManager.userExistsByEmail(request.getEmail()) && users.isEmpty()){
//            User user = new User();
//            user.setUserId(keyCloakUserService.createUser(request).getId());
//            user.setEmail(request.getEmail());
//            user.setVerified(true);
//            user.setDisabled(false);
//            user.setFirstName(request.getFirstName());
//            user.setLastName(request.getLastName());
//            userManager.saveUser(user);
//            keyCloakUserService.createPassword(getUserRegistrationRequest().getEmail(), SUPER_ADMIN_PASSWORD);
//        }
//    }
}
