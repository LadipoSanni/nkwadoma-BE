package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class OrganizationIdentityService implements CreateOrganizationUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;


    @Override
        public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        validateOrganizationIdentityDetails(organizationIdentity);

        organizationIdentity = createOrganizationIdentityOnKeycloak(organizationIdentity);
        log.info("OrganizationIdentity created on keycloak {}", organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = saveOrganisationIdentityToDatabase(organizationIdentity);
        log.info("OrganizationEmployeeIdentity created on the db {}", organizationEmployeeIdentity);
//        sendOrganizationEmployeeEmailUseCase.sendEmail(organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser());
        sendOrganizationEmployeeEmailUseCase.sendEmail(organizationEmployeeIdentity.getMeedlUser());
        log.info("sent email");
        log.info("organization identity saved is : {}",organizationIdentity);
       return organizationIdentity;
    }

    @Override
    public OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        List<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeIdentityOutputPort.findAllByOrganization(organizationId);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        log.info("found organization employees: {}",organizationEmployees);
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                            try {
                                log.info("Deactivating user {}", organizationEmployeeIdentity.getMiddlUser());
                                organizationEmployeeIdentity.getMiddlUser().setDeactivationReason(reason);
                                identityManagerOutPutPort.disableUserAccount(organizationEmployeeIdentity.getMiddlUser());
                            } catch (MeedlException e) {
                                log.error("Error disabling organization user : {}", e.getMessage());
                            }
                        });

        identityManagerOutPutPort.disableOrganization(foundOrganization);
        foundOrganization.setEnabled(Boolean.FALSE);
        return foundOrganization;
    }

    private void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MeedlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        UserIdentityValidator.validateUserIdentity(organizationIdentity.getOrganizationEmployees());
        log.info("Organization service validated is : {}",organizationIdentity);
    }

    private OrganizationIdentity createOrganizationIdentityOnKeycloak(OrganizationIdentity organizationIdentity) throws MeedlException {
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        organizationIdentity = identityManagerOutPutPort.createOrganization(organizationIdentity);
        log.info("OrganizationEmployeeIdentity created on keycloak ---------- {}", employeeIdentity);
        UserIdentity newUser = identityManagerOutPutPort.createUser(employeeIdentity.getMeedlUser());
        employeeIdentity.setMeedlUser(newUser);
        employeeIdentity.setOrganization(organizationIdentity.getId());
        return organizationIdentity;
    }

    private OrganizationEmployeeIdentity saveOrganisationIdentityToDatabase(OrganizationIdentity organizationIdentity) throws MeedlException {
        organizationIdentity.setEnabled(Boolean.TRUE);
        organizationIdentityOutputPort.save(organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        organizationEmployeeIdentity.getMeedlUser().setCreatedAt(LocalDateTime.now().toString());
        userIdentityOutputPort.save(organizationEmployeeIdentity.getMeedlUser());
        organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        organizationIdentity.getOrganizationEmployees().get(0).setId(organizationEmployeeIdentity.getId());


        return organizationEmployeeIdentity;
    }



}
