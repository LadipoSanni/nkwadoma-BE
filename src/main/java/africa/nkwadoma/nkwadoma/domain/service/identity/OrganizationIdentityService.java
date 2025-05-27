package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceAlreadyExistsException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;


@RequiredArgsConstructor
@Slf4j
@Component
public class OrganizationIdentityService implements OrganizationUseCase, ViewOrganizationUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    private final ViewOrganizationEmployeesUseCase employeesUseCase;
    private final LoanMetricsUseCase  loanMetricsUseCase;
    private final MeedlNotificationUsecase meedlNotificationUsecase;


    @Override
    public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        validateOrganizationIdentityDetails(organizationIdentity);
        validateUniqueValues(organizationIdentity);
        checkIfOrganizationAndAdminExist(organizationIdentity);
        log.info("After success full validation and check that user or organization doesn't exists");
        organizationIdentity = createOrganizationIdentityOnKeycloak(organizationIdentity);
        log.info("OrganizationIdentity created on keycloak {}", organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = saveOrganisationIdentityToDatabase(organizationIdentity);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
        organizationIdentity.setServiceOfferings(serviceOfferings);
        log.info("OrganizationEmployeeIdentity created on the db {}", organizationEmployeeIdentity);
//        sendOrganizationEmployeeEmailUseCase.sendEmail(organizationEmployeeIdentity.getMeedlUser());
        log.info("sent email");
        log.info("organization identity saved is : {}", organizationIdentity);
        log.info("about to create Loan Metrics for organization : {}", organizationIdentity);
        LoanMetrics loanMetrics = loanMetricsUseCase.createLoanMetrics(organizationIdentity.getId());
        log.info("loan metrics was created successfully for organiozation : {}", loanMetrics.getOrganizationId());
        notifyPortfolioManager(organizationIdentity, NotificationFlag.INVITE_ORGANIZATION);
        return organizationIdentity;
    }

    private void notifyPortfolioManager(OrganizationIdentity organizationIdentity, NotificationFlag notificationFlag) throws MeedlException {
        List<UserIdentity> portfolioManagers = userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);
        for (UserIdentity portfolioManager : portfolioManagers) {
            MeedlNotification notification = MeedlNotification.builder()
                    .user(portfolioManager)
                    .timestamp(LocalDateTime.now())
                    .contentId(organizationIdentity.getId())
                    .senderMail(organizationIdentity.getEmail())
                    .senderFullName(organizationIdentity.getName())
                    .title("New organization with the name " + organizationIdentity.getName() + " added to organizations")
                    .notificationFlag(notificationFlag)
                    .build();
            meedlNotificationUsecase.sendNotification(notification);
        }
    }

    private void checkIfOrganizationAndAdminExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        try {
            checkOrganizationExist(organizationIdentity);
        }catch (MeedlException e){
            if (e.getMessage().equals(ORGANIZATION_NOT_FOUND.getMessage())) {
                log.info("The organization is not previously existing with message: {} orgamization name {}", e.getMessage(), organizationIdentity.getName());
            }else {
                log.error("An exception occurred while trying to check if it is a new organisation");
                throw new MeedlException(e.getMessage());
            }
        }
        checkIfUserAlreadyExist(organizationIdentity);

    }

    private void checkIfUserAlreadyExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        Optional<UserIdentity> optionalUserIdentity = identityManagerOutPutPort.getUserByEmail(organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getEmail());
        if (optionalUserIdentity.isPresent()) {
            log.error("Before creating organization : {}, for user with id {} ", USER_IDENTITY_ALREADY_EXISTS.getMessage(), optionalUserIdentity.get().getId()  );
            throw new ResourceAlreadyExistsException(USER_IDENTITY_ALREADY_EXISTS.getMessage());
        }else {
            log.info("User has not been previously saved. The application can proceed to creating user and making user the first admin for {} organization. ", organizationIdentity.getName());
        }
    }

    private void checkOrganizationExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        ClientRepresentation clientRepresentation = identityManagerOutPutPort.getClientRepresentationByName(organizationIdentity.getName());
        if (organizationIdentity.getName().equals(clientRepresentation.getName())) {
            log.error("OrganizationIdentity already exists, before trying to create organization with name {} ", organizationIdentity.getName());
            throw new MeedlException("Organization already exists");
        }
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
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.ORGANIZATION_NAME_IS_REQUIRED.getMessage());
        MeedlValidator.validateDataElement(reason, "Please provide a reason for reactivating organization.");
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        List<OrganizationEmployeeIdentity> organizationEmployees = foundOrganization.getOrganizationEmployees();
        log.info("found organization employees to reactivate: {}",organizationEmployees.size());
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                    try {
                        log.info("Reactivating user {}", organizationEmployeeIdentity.getMeedlUser());
                        organizationEmployeeIdentity.getMeedlUser().setReactivationReason(reason);
                        organizationEmployeeIdentity.setStatus(ActivationStatus.ACTIVE);
                        organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
                        log.info("Updated Organization employee status: {}", organizationEmployeeIdentity.getStatus());
                        identityManagerOutPutPort.enableUserAccount(organizationEmployeeIdentity.getMeedlUser());
                    } catch (MeedlException e) {
                        log.error("Error enabling organization user : {}", e.getMessage());
                    }
                });

        identityManagerOutPutPort.enableClient(foundOrganization);
        foundOrganization.setEnabled(Boolean.TRUE);
        foundOrganization.setStatus(ActivationStatus.ACTIVE);
        foundOrganization.setTimeUpdated(LocalDateTime.now());
        organizationIdentityOutputPort.save(foundOrganization);
        log.info("Updated Organization entity status: {}", foundOrganization.getStatus());
        return foundOrganization;
    }

    @Override
    public OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        log.info("Reason for deactivating organization is {}", reason);
        MeedlValidator.validateDataElement(reason, "Deactivation reason is required");
        log.info("Deactivation reason : {} validated", reason);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        List<OrganizationEmployeeIdentity> organizationEmployees = foundOrganization.getOrganizationEmployees();
        log.info("Found organization employees: {}", organizationEmployees);
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                            UserIdentity userIdentity = organizationEmployeeIdentity.getMeedlUser();
                            try {
                                log.info("Deactivating user {} , while deactivating organization.", organizationEmployeeIdentity.getMeedlUser());
                                userIdentity.setDeactivationReason(reason);
                                log.info("Reason on deactivating user before deactivating organization is {}", organizationEmployeeIdentity.getMeedlUser().getDeactivationReason());
                                organizationEmployeeIdentity.setStatus(ActivationStatus.DEACTIVATED);
                                organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
                                log.info("Updated organization status: {}", organizationEmployeeIdentity.getStatus());
                                identityManagerOutPutPort.disableUserAccount(userIdentity);
                            } catch (MeedlException e) {
                                log.error("Error disabling organization user : {}", e.getMessage());
                            }
                        });

        identityManagerOutPutPort.disableClient(foundOrganization);
        foundOrganization.setEnabled(Boolean.FALSE);
        foundOrganization.setStatus(ActivationStatus.DEACTIVATED);
        organizationIdentityOutputPort.save(foundOrganization);
        return foundOrganization;
    }

    private void validateOrganizationIdentityDetails(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());
        log.info("Organization service validated is : {}",organizationIdentity);
    }

    private OrganizationIdentity createOrganizationIdentityOnKeycloak(OrganizationIdentity organizationIdentity) throws MeedlException {
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        organizationIdentity = identityManagerOutPutPort.createKeycloakClient(organizationIdentity);
        log.info("OrganizationEmployeeIdentity created on keycloak ---------- {}", employeeIdentity);
        UserIdentity newUser = identityManagerOutPutPort.createUser(employeeIdentity.getMeedlUser());
        log.info("User identity created for this organization. User id: {}. User is new admin", newUser.getId());
        employeeIdentity.setMeedlUser(newUser);
        employeeIdentity.setOrganization(organizationIdentity.getId());
        return organizationIdentity;
    }

    private OrganizationEmployeeIdentity saveOrganisationIdentityToDatabase(OrganizationIdentity organizationIdentity) throws MeedlException {
        organizationIdentity.setEnabled(Boolean.TRUE);
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setStatus(ActivationStatus.INVITED);
        organizationIdentityOutputPort.save(organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        organizationEmployeeIdentity.setStatus(ActivationStatus.INVITED);
        organizationEmployeeIdentity.getMeedlUser().setCreatedAt(LocalDateTime.now());
        userIdentityOutputPort.save(organizationEmployeeIdentity.getMeedlUser());
        organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        organizationIdentity.getOrganizationEmployees().get(0).setId(organizationEmployeeIdentity.getId());
        return organizationEmployeeIdentity;
    }

    @Override
    public OrganizationIdentity updateOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateUUID(organizationIdentity.getUpdatedBy(), MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        validateNonUpdatableValues(organizationIdentity);
        log.info("Organization identity input: {}", organizationIdentity);
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationIdentity.getId());
        foundOrganization = organizationIdentityMapper.updateOrganizationIdentity(foundOrganization, organizationIdentity);
        foundOrganization.setTimeUpdated(LocalDateTime.now());
        log.info("Updated organization: {}", foundOrganization);
        return organizationIdentityOutputPort.save(foundOrganization);
    }

    public void updateOrganizationStatus(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(organizationIdentity.getUserIdentity(), UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(organizationIdentity.getUserIdentity().getId());
        if(ObjectUtils.isNotEmpty(foundUserIdentity) &&
                foundUserIdentity.getRole() == IdentityRole.ORGANIZATION_ADMIN ||
                foundUserIdentity.getRole() == IdentityRole.PORTFOLIO_MANAGER)
        {

            OrganizationEmployeeIdentity employeeIdentity = updateEmployeeStatus(foundUserIdentity);
            updateOrganizationStatus(organizationIdentity, employeeIdentity);
        }
    }

    private void updateOrganizationStatus(OrganizationIdentity organizationIdentity, OrganizationEmployeeIdentity employeeIdentity) throws MeedlException {
        OrganizationIdentity foundOrganizationIdentity =
                viewOrganizationDetails(employeeIdentity.getOrganization());
        log.info("Found organization: {}", foundOrganizationIdentity);
        if (foundOrganizationIdentity.getStatus() != ActivationStatus.ACTIVE) {
            log.info("Organization found is not activated with id {} and status {}", foundOrganizationIdentity.getId(), foundOrganizationIdentity.getStatus());
            foundOrganizationIdentity.setStatus(ActivationStatus.ACTIVE);
            foundOrganizationIdentity.setUpdatedBy(organizationIdentity.getUserIdentity().getId());
            foundOrganizationIdentity.setTimeUpdated(LocalDateTime.now());
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(foundOrganizationIdentity);
            log.info("Updated Organization Entity Status: {}", savedOrganization.getStatus());
        }
    }

    private OrganizationEmployeeIdentity updateEmployeeStatus(UserIdentity userIdentity) throws MeedlException {
        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().
                id(userIdentity.getId()).build();
        employeeIdentity = employeesUseCase.viewEmployeeDetails(employeeIdentity);
        log.info("Found Employee identity to update status: {}", employeeIdentity);

        employeeIdentity.setStatus(ActivationStatus.ACTIVE);
        employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
        log.info("Updated Organization Employee Status: {}", employeeIdentity.getStatus());
        return employeeIdentity;
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
    public Page<OrganizationIdentity> viewAllOrganizationByStatus(OrganizationIdentity organizationIdentity, ActivationStatus activationStatus) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(activationStatus, OrganizationMessages.ORGANIZATION_STATUS_MUST_NOT_BE_EMPTY.getMessage());
        return organizationIdentityOutputPort.viewAllOrganizationByStatus(organizationIdentity, activationStatus);
    }

    @Override
    public Page<OrganizationIdentity> search(OrganizationIdentity organizationIdentity) throws MeedlException {
        if (ObjectUtils.isNotEmpty(organizationIdentity.getLoanType())){
            return organizationIdentityOutputPort.findByNameSortingByLoanType(organizationIdentity.getName()
                    ,organizationIdentity.getLoanType(),organizationIdentity.getPageSize(),organizationIdentity.getPageNumber());
        }
        return organizationIdentityOutputPort.findByName(organizationIdentity.getName(),organizationIdentity.getStatus()
                ,organizationIdentity.getPageSize(),organizationIdentity.getPageNumber());
    }

    @Override
    public OrganizationIdentity viewOrganizationDetails(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
        organizationIdentity.setServiceOfferings(serviceOfferings);
        return organizationIdentity;
    }

    @Override
    public OrganizationIdentity viewTopOrganizationByLoanRequestCount() throws MeedlException {
        Optional<LoanMetrics> loanMetrics = loanMetricsOutputPort.findTopOrganizationWithLoanRequest();
        if (loanMetrics.isEmpty()){
            throw new EducationException(OrganizationMessages.LOAN_METRICS_NOT_FOUND.getMessage());
        }
        log.info("Loan metrics found: {}", loanMetrics);
        return organizationIdentityOutputPort.findById(loanMetrics.get().getOrganizationId());
    }

    @Override
    public Page<OrganizationIdentity> viewAllOrganizationsLoanMetrics(LoanType loanType,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanType,"Loan type cannot be empty");
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Page<OrganizationIdentity> organizationIdentities = organizationIdentityOutputPort.findAllWithLoanMetrics(loanType,pageSize,pageNumber);
        log.info("Organizations returned: {}", organizationIdentities);
        return organizationIdentities;
    }

    @Override
    public OrganizationIdentity viewOrganizationDetailsByOrganizationAdmin(String adminId) throws MeedlException {
        MeedlValidator.validateUUID(adminId, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                organizationEmployeeIdentityOutputPort.findByCreatedBy(adminId);
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());
        organizationIdentity.setOrganizationEmployees(organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId()));
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
        log.info("Total number loanees {}",organizationIdentity.getNumberOfLoanees());
        log.info("Total number Programs {}",organizationIdentity.getNumberOfPrograms());
        organizationIdentity.setServiceOfferings(serviceOfferings);
        return organizationIdentity;
    }
}
