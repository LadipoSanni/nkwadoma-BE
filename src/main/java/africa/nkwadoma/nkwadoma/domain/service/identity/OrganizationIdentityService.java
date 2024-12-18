package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.ViewOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;

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
        validateUniqueValues(organizationIdentity);
        organizationIdentity = createOrganizationIdentityOnKeycloak(organizationIdentity);
        log.info("OrganizationIdentity created on keycloak {}", organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = saveOrganisationIdentityToDatabase(organizationIdentity);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity);
        organizationIdentity.setServiceOfferings(serviceOfferings);
        log.info("OrganizationEmployeeIdentity created on the db {}", organizationEmployeeIdentity);
        sendOrganizationEmployeeEmailUseCase.sendEmail(organizationEmployeeIdentity.getMeedlUser());
        log.info("sent email");
        log.info("organization identity saved is : {}",organizationIdentity);
       return organizationIdentity;
    }

    private void validateUniqueValues(OrganizationIdentity organizationIdentity) throws MeedlException {
        Optional<OrganizationEntity> foundOrganizationEntity =
                organizationIdentityOutputPort.findByRcNumber(organizationIdentity.getRcNumber());
        if (foundOrganizationEntity.isPresent()) {
            log.info("Organization with rc number {} already exists", foundOrganizationEntity.get().getRcNumber());
            throw new MeedlException(ORGANIZATION_RC_NUMBER_ALREADY_EXIST.getMessage());
        }

        Optional<OrganizationIdentity> foundOrganizationIdentity =
                organizationIdentityOutputPort.findByTin(organizationIdentity.getTin());
        if (foundOrganizationIdentity.isPresent()) {
            throw new MeedlException(IdentityMessages.ORGANIZATION_TIN_ALREADY_EXIST.getMessage());
        }
    }

    @Override
    public OrganizationIdentity reactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validateDataElement(reason);
        List<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeIdentityOutputPort.findAllByOrganization(organizationId);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        log.info("found organization employees to reactivate: {}",organizationEmployees.size());
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                    try {
                        log.info("Reactivating user {}, while reactivating organization", organizationEmployeeIdentity.getMeedlUser());
                        organizationEmployeeIdentity.getMeedlUser().setReactivationReason(reason);
                        identityManagerOutPutPort.enableUserAccount(organizationEmployeeIdentity.getMeedlUser());
                    } catch (MeedlException e) {
                        log.error("Error enabling organization user : {}", e.getMessage());
                    }
                });

        identityManagerOutPutPort.enableClient(foundOrganization);
        foundOrganization.setEnabled(Boolean.TRUE);
        foundOrganization.setStatus(ActivationStatus.ACTIVATED);
        organizationIdentityOutputPort.save(foundOrganization);
        log.info("Organization reactivated successfully. Organization id : {}", organizationId);
        return foundOrganization;
    }

    @Override
    public OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateDataElement(reason, "Deactivation reason is required");
        List<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeIdentityOutputPort.findAllByOrganization(organizationId);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        log.info("found organization employees: {}",organizationEmployees);
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                            try {
                                log.info("Deactivating user {} , while deactivating organization.", organizationEmployeeIdentity.getMeedlUser());
                                organizationEmployeeIdentity.getMeedlUser().setDeactivationReason(reason);
                                identityManagerOutPutPort.disableUserAccount(organizationEmployeeIdentity.getMeedlUser());
                            } catch (MeedlException e) {
                                log.error("Error disabling organization user : {}", e.getMessage());
                            }
                        });

        identityManagerOutPutPort.disableClient(foundOrganization);
        foundOrganization.setEnabled(Boolean.FALSE);
        foundOrganization.setStatus(ActivationStatus.DEACTIVATED);
        organizationIdentityOutputPort.save(foundOrganization);
        log.info("Organization deactivated successfully. Organization id : {}", organizationId);
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
        organizationIdentity.setStatus(ActivationStatus.INVITED);
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
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getUpdatedBy(), MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
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
    public List<OrganizationIdentity> search(String organizationName) throws MeedlException {
        MeedlValidator.validateDataElement(organizationName, "Organization name is required");
        return organizationIdentityOutputPort.findByName(organizationName);
    }
    @Override
    public OrganizationIdentity viewOrganizationDetails(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity);
        organizationIdentity.setServiceOfferings(serviceOfferings);
        return organizationIdentity;
    }

    @Override
    public OrganizationIdentity viewOrganizationDetailsByOrganizationAdmin(String adminId) throws MeedlException {
        MeedlValidator.validateUUID(adminId, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByCreatedBy(adminId);
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
        organizationIdentity.setOrganizationEmployees(organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId()));
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity);
        log.info("Total number loanees {}",organizationIdentity.getNumberOfLoanees());
        log.info("Total number Programs {}",organizationIdentity.getNumberOfPrograms());
        organizationIdentity.setServiceOfferings(serviceOfferings);
        return organizationIdentity;
    }
}
