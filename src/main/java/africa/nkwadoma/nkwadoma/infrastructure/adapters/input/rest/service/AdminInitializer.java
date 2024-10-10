package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {
    private final CreateOrganizationUseCase createOrganizationUseCase;

//    @Value("${superAdmin.email}")
    private String SUPER_ADMIN_EMAIL = "email@email.com";

//    @Value("${superAdmin.firstName}")
    private String SUPER_ADMIN_FIRST_NAME = "first name";

//    @Value("${superAdmin.lastName}")
    private String SUPER_ADMIN_LAST_NAME = "Last name";

    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .firstName(SUPER_ADMIN_FIRST_NAME)
                .lastName(SUPER_ADMIN_LAST_NAME)
                .role(PORTFOLIO_MANAGER.name())
                .createdBy("ned")
                .build();
    }
    private OrganizationEmployeeIdentity getOrganizationEmployeeIdentity() {
        return OrganizationEmployeeIdentity.builder()
                .middlUser(getUserIdentity())
                .build();
    }
    private OrganizationIdentity getOrganizationIdentity() {

        return OrganizationIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .name("Middl")
                .phoneNumber("nil")
                .industry("Middl")
                .rcNumber("nil")
                .organizationEmployees(List.of(getOrganizationEmployeeIdentity()))
                .build();
    }
    public OrganizationIdentity inviteFirstUser(OrganizationIdentity organizationIdentity) throws MiddlException {
        return createOrganizationUseCase.inviteOrganization(organizationIdentity);
    }
//    @PostConstruct
    public void init() throws MiddlException {
        inviteFirstUser(getOrganizationIdentity());

    }
}
