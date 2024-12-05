package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.ViewOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_RC_NUMBER_ALREADY_EXIST;


@RequiredArgsConstructor
@Slf4j
public class OrganizationIdentityService implements CreateOrganizationUseCase, ViewOrganizationUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;


    @Override
        public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        validateOrganizationIdentityDetails(organizationIdentity);
        Optional<OrganizationEntity> foundOrganizationEntity = organizationIdentityOutputPort.findByRcNumber(organizationIdentity.getRcNumber());
        if (foundOrganizationEntity.isPresent()) {
            throw new MeedlException(ORGANIZATION_RC_NUMBER_ALREADY_EXIST.getMessage());
        }
        organizationIdentity = createOrganizationIdentityOnKeycloak(organizationIdentity);
        log.info("OrganizationIdentity created on keycloak {}", organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = saveOrganisationIdentityToDatabase(organizationIdentity);
        log.info("OrganizationEmployeeIdentity created on the db {}", organizationEmployeeIdentity);
        sendOrganizationEmployeeEmailUseCase.sendEmail(organizationEmployeeIdentity.getMeedlUser());
        log.info("sent email");
        log.info("organization identity saved is : {}",organizationIdentity);
       return organizationIdentity;
    }

    @Override
    public OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validateDataElement(reason);
        List<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeIdentityOutputPort.findAllByOrganization(organizationId);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        log.info("found organization employees: {}",organizationEmployees);
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                            try {
                                log.info("Deactivating user {}", organizationEmployeeIdentity.getMeedlUser());
                                organizationEmployeeIdentity.getMeedlUser().setDeactivationReason(reason);
                                identityManagerOutPutPort.disableUserAccount(organizationEmployeeIdentity.getMeedlUser());
                            } catch (MeedlException e) {
                                log.error("Error disabling organization user : {}", e.getMessage());
                            }
                        });

        identityManagerOutPutPort.disableClient(foundOrganization);
        foundOrganization.setEnabled(Boolean.FALSE);
        foundOrganization.setStatus(ActivationStatus.DEACTIVATED);
        return foundOrganization;
    }

    private void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity);
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());
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
    @Override
    public OrganizationIdentity updateOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity);
        MeedlValidator.validateUUID(organizationIdentity.getId());
        MeedlValidator.validateUUID(organizationIdentity.getUpdatedBy());
        validateNonUpdatableValues(organizationIdentity);
        log.info("Organization identity input: {}", organizationIdentity);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationIdentity.getId());
        foundOrganization = organizationIdentityMapper.updateOrganizationIdentity(foundOrganization,organizationIdentity);
        foundOrganization.setTimeUpdated(LocalDateTime.now());
        log.info("Updated organization: {}", foundOrganization);
        return organizationIdentityOutputPort.save(foundOrganization);
    }

    private void validateNonUpdatableValues(OrganizationIdentity organizationIdentity) throws MeedlException {
        if (StringUtils.isNotEmpty(organizationIdentity.getName())) {
            throw new MeedlException("Company name cannot be updated!");
        }
        if (StringUtils.isNotEmpty(organizationIdentity.getRcNumber())) {
            throw new MeedlException("Rc number cannot be updated!");
        }
    }

    @Override
    public Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        return organizationIdentityOutputPort.viewAllOrganization(organizationIdentity);
    }

    @Override
    public OrganizationIdentity viewOrganizationDetailsByOrganizationAdmin(String adminId) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByCreatedBy(adminId);
        return organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
    }

    @Override
    public List<OrganizationIdentity> search(String organizationName) throws MeedlException {
        MeedlValidator.validateDataElement(organizationName);
        return organizationIdentityOutputPort.findByName(organizationName);
    }
    @Override
    public OrganizationIdentity viewOrganizationDetails(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        return organizationIdentityOutputPort.findById(organizationId);
    }
}
