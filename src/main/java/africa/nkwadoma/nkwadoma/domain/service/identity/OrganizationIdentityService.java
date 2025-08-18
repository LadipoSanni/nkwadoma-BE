package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;


@RequiredArgsConstructor
@Slf4j
@Component
@EnableAsync
public class OrganizationIdentityService implements OrganizationUseCase, ViewOrganizationUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final ViewOrganizationEmployeesUseCase employeesUseCase;
    private final LoanMetricsUseCase  loanMetricsUseCase;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final MeedlNotificationOutputPort meedlNotificationOutputPort;



    @Override
    public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        validateOrganizationIdentityDetails(organizationIdentity);
        validateUniqueValues(organizationIdentity);
        checkIfOrganizationAndAdminExist(organizationIdentity);

        UserIdentity userIdentity = userIdentityOutputPort.findById(organizationIdentity.getCreatedBy());

        log.info("After success full validation and check that user or organization doesn't exists");
        organizationIdentity = createOrganizationIdentityOnKeycloak(organizationIdentity);
        log.info("OrganizationIdentity created on keycloak {}", organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = saveOrganisationIdentityToDatabase(organizationIdentity,userIdentity.getRole());
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
        organizationIdentity.setServiceOfferings(serviceOfferings);
        log.info("OrganizationEmployeeIdentity created on the db {}", organizationEmployeeIdentity);

        if (userIdentity.getRole().equals(IdentityRole.MEEDL_SUPER_ADMIN)){
            asynchronousMailingOutputPort.sendEmailToInvitedOrganization(organizationEmployeeIdentity.getMeedlUser());
            log.info("sent email");
        }

        log.info("organization identity saved is : {}", organizationIdentity);
        log.info("about to create Loan Metrics for organization : {}", organizationIdentity);
        LoanMetrics loanMetrics = loanMetricsUseCase.createLoanMetrics(organizationIdentity.getId());
        log.info("loan metrics was created successfully for organiozation : {}", loanMetrics.getOrganizationId());
        OrganizationLoanDetail organizationLoanDetail = buildOrganizationLoanDetail(organizationIdentity);
        organizationLoanDetailOutputPort.save(organizationLoanDetail);
        if (! userIdentity.getRole().equals(IdentityRole.MEEDL_SUPER_ADMIN)) {
            asynchronousNotificationOutputPort.notifySuperAdminOfNewOrganization(
                    userIdentity,organizationIdentity, NotificationFlag.APPROVE_INVITE_ORGANIZATION);
        }
        return organizationIdentity;
    }

    private OrganizationLoanDetail buildOrganizationLoanDetail(OrganizationIdentity organizationIdentity) {
        return OrganizationLoanDetail.builder()
                .organization(organizationIdentity).amountReceived(BigDecimal.valueOf(0))
                .amountRequested(BigDecimal.valueOf(0)).amountRepaid(BigDecimal.valueOf(0))
                .interestIncurred(BigDecimal.ZERO)
                .outstandingAmount(BigDecimal.valueOf(0)).build();
    }

    private void checkIfOrganizationAndAdminExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        try {
            checkOrganizationExist(organizationIdentity);
        }catch (IdentityException e){
            if (e.getMessage().equals(ORGANIZATION_NOT_FOUND.getMessage())) {
                log.info("The organization is not previously existing with message: {} orgamization name {}", e.getMessage(), organizationIdentity.getName());
            }else {
                log.error("An exception occurred while trying to check if it is a new organisation");
                throw new IdentityException(e.getMessage());
            }
        }
        checkIfUserAlreadyExist(organizationIdentity);

    }

    private void checkIfUserAlreadyExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        Optional<UserIdentity> optionalUserIdentity = identityManagerOutPutPort.getUserByEmail(organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getEmail());
        if (optionalUserIdentity.isPresent()) {
            log.error("Before creating organization : {}, for user with id {} ", USER_IDENTITY_ALREADY_EXISTS.getMessage(), optionalUserIdentity.get().getId()  );
            throw new IdentityException(USER_IDENTITY_ALREADY_EXISTS.getMessage());
        }else {
            log.info("User has not been previously saved. The application can proceed to creating user and making user the first admin for {} organization. ", organizationIdentity.getName());
        }
    }

    private void checkOrganizationExist(OrganizationIdentity organizationIdentity) throws MeedlException {
        ClientRepresentation clientRepresentation = identityManagerOutPutPort.getClientRepresentationByName(organizationIdentity.getName());
        if (organizationIdentity.getName().equals(clientRepresentation.getName())) {
            log.error("OrganizationIdentity already exists, before trying to create organization with name {} ", organizationIdentity.getName());
            throw new IdentityException("Organization already exists");
        }
    }

    private void validateUniqueValues(OrganizationIdentity organizationIdentity) throws MeedlException {
        Optional<OrganizationEntity> foundOrganizationEntity =
                organizationIdentityOutputPort.findByRcNumber(organizationIdentity.getRcNumber());
        if (foundOrganizationEntity.isPresent()) {
            log.info("Organization with rc number {} already exists", foundOrganizationEntity.get().getRcNumber());
            throw new IdentityException(ORGANIZATION_RC_NUMBER_ALREADY_EXIST.getMessage());
        }

        Optional<OrganizationIdentity> foundOrganizationIdentity =
                organizationIdentityOutputPort.findByTin(organizationIdentity.getTin());
        if (foundOrganizationIdentity.isPresent()) {
            throw new IdentityException(IdentityMessages.ORGANIZATION_TIN_ALREADY_EXIST.getMessage());
        }
    }
    @Override
    public OrganizationIdentity reactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.ORGANIZATION_NAME_IS_REQUIRED.getMessage());
        MeedlValidator.validateDataElement(reason, "Please provide a reason for reactivating organization.");
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        List<OrganizationEmployeeIdentity> organizationEmployees = foundOrganization.getOrganizationEmployees();
        log.info("found organization employees to reactivate: {}",organizationEmployees.size());

        reactivateOrganizationEmployees(reason, organizationEmployees);
        updateOrganizationActivationStatus(foundOrganization,ActivationStatus.ACTIVE);
        asynchronousMailingOutputPort.sendReactivatedEmployeesEmailNotification(organizationEmployees, foundOrganization);
        log.info("Updated Organization entity status: {}", foundOrganization.getActivationStatus());
        return foundOrganization;
    }

    @Override
    public OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateDataElement(reason, "Deactivation reason is required");
        log.info("Deactivation reason : {} validated", reason);

        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findById(organizationId);
        List<OrganizationEmployeeIdentity> organizationEmployees = foundOrganization.getOrganizationEmployees();
        log.info("Found organization employees: {}", organizationEmployees);

        deactivateOrganizationEmployees(reason, organizationEmployees);
        updateOrganizationActivationStatus(foundOrganization,ActivationStatus.DEACTIVATED);
        asynchronousMailingOutputPort.sendDeactivatedEmployeesEmailNotification(organizationEmployees, foundOrganization);
        return foundOrganization;
    }
    private void reactivateOrganizationEmployees(String reason, List<OrganizationEmployeeIdentity> organizationEmployees) {
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                    try {
                        reactivateOrganizationEmployee(organizationEmployeeIdentity, reason);
                    } catch (MeedlException e) {
                        log.error("Error enabling organization user : {}", e.getMessage());
                    }
                });
    }

    private void deactivateOrganizationEmployees(String reason, List<OrganizationEmployeeIdentity> organizationEmployees) {
        organizationEmployees
                .forEach(organizationEmployeeIdentity -> {
                            UserIdentity userIdentity = organizationEmployeeIdentity.getMeedlUser();
                            try {
                                deactivateOrganizationEmployee(reason, organizationEmployeeIdentity, userIdentity);
                            } catch (MeedlException e) {
                                log.error("Error disabling organization user : {}", e.getMessage());
                            }
                        });
    }

    private void updateOrganizationActivationStatus(OrganizationIdentity organization, ActivationStatus activationStatus) throws MeedlException {
        if (activationStatus == ActivationStatus.ACTIVE) {
            identityManagerOutPutPort.enableClient(organization);
            organization.setEnabled(Boolean.TRUE);
            organization.setActivationStatus(ActivationStatus.ACTIVE);
        } else {
            identityManagerOutPutPort.disableClient(organization);
            organization.setEnabled(Boolean.FALSE);
            organization.setActivationStatus(ActivationStatus.DEACTIVATED);
        }

        organization.setTimeUpdated(LocalDateTime.now());
        organizationIdentityOutputPort.save(organization);
    }

    private void reactivateOrganizationEmployee(OrganizationEmployeeIdentity organizationEmployeeIdentity, String reason) throws MeedlException {
        log.info("Reactivating user {}", organizationEmployeeIdentity.getMeedlUser());
        organizationEmployeeIdentity.getMeedlUser().setReactivationReason(reason);
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        log.info("Updated Organization employee status: {}", organizationEmployeeIdentity.getActivationStatus());
        identityManagerOutPutPort.enableUserAccount(organizationEmployeeIdentity.getMeedlUser());
    }


    private void deactivateOrganizationEmployee(String reason, OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity userIdentity) throws MeedlException {
        log.info("Deactivating user {} , while deactivating organization.", organizationEmployeeIdentity.getMeedlUser());
        userIdentity.setDeactivationReason(reason);
        log.info("Reason on deactivating user before deactivating organization is {}", organizationEmployeeIdentity.getMeedlUser().getDeactivationReason());
        organizationEmployeeIdentity.setActivationStatus(ActivationStatus.DEACTIVATED);
        organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        log.info("Updated organization status: {}", organizationEmployeeIdentity.getActivationStatus());
        identityManagerOutPutPort.disableUserAccount(userIdentity);
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

    private OrganizationEmployeeIdentity saveOrganisationIdentityToDatabase(OrganizationIdentity organizationIdentity,IdentityRole identityRole) throws MeedlException {
        organizationIdentity.setEnabled(Boolean.TRUE);
        if (identityRole.equals(IdentityRole.MEEDL_SUPER_ADMIN)) {
            organizationIdentity.setActivationStatus(ActivationStatus.INVITED);
            organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        }else {
            organizationIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        }
        organizationIdentityOutputPort.save(organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        if (identityRole.equals(IdentityRole.MEEDL_SUPER_ADMIN)) {
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.INVITED);
        }else {
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        }
        organizationEmployeeIdentity.getMeedlUser().setCreatedAt(LocalDateTime.now());
        userIdentityOutputPort.save(organizationEmployeeIdentity.getMeedlUser());
        organizationEmployeeIdentity.setCreatedBy(organizationEmployeeIdentity.getMeedlUser().getCreatedBy());
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
        log.info("Updating organization status during create password flow {} \n -------------------------------------> found user role is {}", organizationIdentity, foundUserIdentity.getRole());
        if(ObjectUtils.isNotEmpty(foundUserIdentity) &&
                foundUserIdentity.getRole() == ORGANIZATION_ADMIN ||
                foundUserIdentity.getRole() == PORTFOLIO_MANAGER)
        {

            OrganizationEmployeeIdentity employeeIdentity = updateEmployeeStatus(foundUserIdentity);
            updateOrganizationStatus(organizationIdentity, employeeIdentity);
        }
    }

    @Override
    public String respondToOrganizationInvite(String actorId, String organizationId, ActivationStatus activationStatus) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectInstance(activationStatus,"Activation status cannot be empty");
        MeedlValidator.validateActivationSatus(activationStatus);

        UserIdentity actor = userIdentityOutputPort.findById(actorId);

        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        UserIdentity organizationCreator = userIdentityOutputPort.findById(organizationIdentity.getCreatedBy());

        checkCurrentStatusOfOrganization(organizationIdentity);

        if (activationStatus.equals(ActivationStatus.APPROVED)){
            approveInvitation(activationStatus, organizationIdentity, organizationCreator, actor);
        }else {
            declineInvitation(activationStatus, organizationCreator, organizationIdentity, actor);

        }
        organizationIdentityOutputPort.save(organizationIdentity);
        return "Invitation "+activationStatus.name();
    }

    @Override
    public String inviteColleague(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Inviting colleague");
        MeedlValidator.validateObjectInstance(organizationIdentity.getUserIdentity(), IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        UserIdentity newColleague = organizationIdentity.getUserIdentity();
        newColleague.validate();

        OrganizationEmployeeIdentity inviter = organizationEmployeeIdentityOutputPort.findByEmployeeId(newColleague.getCreatedBy());
        log.info("Found employee: {}", inviter);

        validateRolePermissions(inviter.getMeedlUser().getRole(), newColleague.getRole());

        newColleague.setCreatedAt(LocalDateTime.now());
        log.info("about to create colleague on keycloak {}", newColleague);
        newColleague = identityManagerOutPutPort.createUser(newColleague);
        log.info("done creating colleague on keycloak {}", newColleague);
        log.info("about to save colleague to DB  {}", newColleague);
        UserIdentity savedUserIdentity = userIdentityOutputPort.save(newColleague);
        log.info("done saving colleague user identity saved to DB: {}", savedUserIdentity);


        log.info("about to set up new colleague representation in organization {}", newColleague);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = buildOrganizationEmployeeIdentity(inviter, newColleague);
        organizationEmployeeIdentity.setCreatedBy(newColleague.getCreatedBy());
        OrganizationEmployeeIdentity savedEmployee = organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
        log.info("Saved new colleague employee identity in organization: {}", savedEmployee);


        return handleNotificationsAndResponse(inviter, savedEmployee, savedUserIdentity);
    }

    private String handleNotificationsAndResponse(OrganizationEmployeeIdentity inviter, OrganizationEmployeeIdentity
            savedEmployee, UserIdentity savedUserIdentity) throws MeedlException {

        IdentityRole inviterRole = inviter.getMeedlUser().getRole();

        if (isSuperAdmin(inviterRole)) {
            log.info("The user inviting is a super admin with organization id {}", inviter.getOrganization());
            OrganizationIdentity organization = organizationIdentityOutputPort.findById(inviter.getOrganization());
            asynchronousMailingOutputPort.sendColleagueEmail(organization.getName(), savedUserIdentity);
            return String.format("Colleague with role %s invited", savedUserIdentity.getRole().name());
        }

        IdentityRole superAdminRole = inviterRole.isMeedlRole()
                ? IdentityRole.MEEDL_SUPER_ADMIN
                : IdentityRole.ORGANIZATION_SUPER_ADMIN;
        OrganizationEmployeeIdentity superAdmin = organizationEmployeeIdentityOutputPort
                .findByRoleAndOrganizationId(inviter.getOrganization(), superAdminRole);

        asynchronousNotificationOutputPort.sendNotificationToSuperAdmin(inviter, savedEmployee, superAdmin);
        return "Invitation needs approval, pending.";
    }


    private void validateRolePermissions(IdentityRole inviterRole, IdentityRole colleagueRole) throws IdentityException {
        Map<IdentityRole, Set<IdentityRole>> allowedRoles = Map.of(
                IdentityRole.MEEDL_SUPER_ADMIN, Set.of(PORTFOLIO_MANAGER, MEEDL_ADMIN, IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE),
                MEEDL_ADMIN, Set.of(PORTFOLIO_MANAGER, IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE,MEEDL_ADMIN),
                PORTFOLIO_MANAGER, Set.of(IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE,PORTFOLIO_MANAGER),
                IdentityRole.ORGANIZATION_SUPER_ADMIN, Set.of(ORGANIZATION_ADMIN, IdentityRole.ORGANIZATION_ASSOCIATE),
                ORGANIZATION_ADMIN, Set.of(IdentityRole.ORGANIZATION_ASSOCIATE,ORGANIZATION_ADMIN)
        );

        if (!allowedRoles.getOrDefault(inviterRole, Set.of()).contains(colleagueRole)) {
            throw new IdentityException(String.format("Role %s cannot invite colleague with role %s", inviterRole, colleagueRole));
        }
    }

    private OrganizationEmployeeIdentity buildOrganizationEmployeeIdentity(
            OrganizationEmployeeIdentity inviter, UserIdentity colleague) {
        log.info("Building organization empty organization employee for the invited employee");
        OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity();
        employeeIdentity.setOrganization(inviter.getOrganization());
        employeeIdentity.setMeedlUser(colleague);
        employeeIdentity.setActivationStatus(isSuperAdmin(inviter.getMeedlUser().getRole())
                ? ActivationStatus.INVITED
                : ActivationStatus.PENDING_APPROVAL);
        log.info("The built organization employee being invited is {}", employeeIdentity);
        return employeeIdentity;
    }

    private boolean isSuperAdmin(IdentityRole role) {
        return role == IdentityRole.MEEDL_SUPER_ADMIN || role == IdentityRole.ORGANIZATION_SUPER_ADMIN;
    }



    private static void checkCurrentStatusOfOrganization(OrganizationIdentity organizationIdentity) throws IdentityException {
        if (!organizationIdentity.getActivationStatus().equals(ActivationStatus.PENDING_APPROVAL) &&
                ! organizationIdentity.getActivationStatus().equals( ActivationStatus.DECLINED)) {
            throw new IdentityException("This organization cannot be activated because its status is neither pending approval nor declined.");
        }
    }

    private void declineInvitation(ActivationStatus activationStatus, UserIdentity organizationCreator, OrganizationIdentity organizationIdentity, UserIdentity actor) throws MeedlException {
        sendNotificationToOrganizationCreator(activationStatus, organizationCreator, organizationIdentity,
                actor,NotificationFlag.ORGANIZATION_INVITATION_DECLINED);
        organizationIdentity.setActivationStatus(ActivationStatus.DECLINED);
    }

    private void approveInvitation(ActivationStatus activationStatus, OrganizationIdentity organizationIdentity, UserIdentity organizationCreator, UserIdentity actor) throws MeedlException {
        organizationIdentity.setActivationStatus(ActivationStatus.INVITED);
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());

        for(OrganizationEmployeeIdentity organizationEmployeeIdentity : organizationIdentity.getOrganizationEmployees()){
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.INVITED);
            organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
            asynchronousMailingOutputPort.sendEmailToInvitedOrganization(organizationEmployeeIdentity.getMeedlUser());
        }
        sendNotificationToOrganizationCreator(activationStatus, organizationCreator, organizationIdentity,
                actor,NotificationFlag.ORGANIZATION_INVITATION_APPROVED );
    }

    private void sendNotificationToOrganizationCreator(ActivationStatus activationStatus, UserIdentity organizationCreator, OrganizationIdentity
            organizationIdentity, UserIdentity actor, NotificationFlag notificationFlag) throws MeedlException {
        MeedlNotification notification = MeedlNotification.builder()
                .user(organizationCreator)
                .contentId(organizationIdentity.getId())
                .callToAction(true)
                .notificationFlag(notificationFlag)
                .timestamp(LocalDateTime.now())
                .senderMail(actor.getEmail())
                .title(notificationFlag.name())
                .senderFullName(actor.getFirstName()+" "+ actor.getLastName())
                .contentDetail("The Invitation for "+ organizationIdentity.getName()+" has been "+ activationStatus.name())
                .build();

        meedlNotificationOutputPort.save(notification);
    }

    private void updateOrganizationStatus(OrganizationIdentity organizationIdentity, OrganizationEmployeeIdentity employeeIdentity) throws MeedlException {
        OrganizationIdentity foundOrganizationIdentity =
                viewOrganizationDetails(employeeIdentity.getOrganization(), organizationIdentity.getUserIdentity().getId());
        log.info("Found organization: {}", foundOrganizationIdentity);
        if (foundOrganizationIdentity.getActivationStatus() != ActivationStatus.ACTIVE) {
            log.info("Organization found is not activated with id {} and status {}", foundOrganizationIdentity.getId(), foundOrganizationIdentity.getActivationStatus());
            foundOrganizationIdentity.setActivationStatus(ActivationStatus.ACTIVE);
            foundOrganizationIdentity.setUpdatedBy(organizationIdentity.getUserIdentity().getId());
            foundOrganizationIdentity.setTimeUpdated(LocalDateTime.now());
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(foundOrganizationIdentity);
            log.info("Updated Organization Entity Status: {}", savedOrganization.getActivationStatus());
        }
    }

    private OrganizationEmployeeIdentity updateEmployeeStatus(UserIdentity userIdentity) throws MeedlException {
        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().
                id(userIdentity.getId()).build();
        employeeIdentity = employeesUseCase.viewEmployeeDetails(employeeIdentity);
        log.info("Found Employee identity to update status: {}", employeeIdentity);

        employeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
        log.info("Updated Organization Employee Status: {}", employeeIdentity.getActivationStatus());
        return employeeIdentity;
    }

    private void validateNonUpdatableValues(OrganizationIdentity organizationIdentity) throws MeedlException {
        if (StringUtils.isNotEmpty(organizationIdentity.getName())) {
            throw new IdentityException("Company name cannot be updated!");
        }
        if (StringUtils.isNotEmpty(organizationIdentity.getRcNumber())) {
            throw new IdentityException("Rc number cannot be updated!");
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
        List <String> activationStatuses = List.of(String.valueOf(activationStatus));
        if (ActivationStatus.INVITED.equals(activationStatus)){
            activationStatuses = List.of(ActivationStatus.INVITED.name(), ActivationStatus.PENDING_APPROVAL.name());
        }
        return organizationIdentityOutputPort.viewAllOrganizationByStatus(organizationIdentity, activationStatuses);
    }

    @Override
    public Page<OrganizationIdentity> search(OrganizationIdentity organizationIdentity) throws MeedlException {
        if (ObjectUtils.isNotEmpty(organizationIdentity.getLoanType())){
            return organizationIdentityOutputPort.findByNameSortingByLoanType(organizationIdentity.getName()
                    ,organizationIdentity.getLoanType(),organizationIdentity.getPageSize(),organizationIdentity.getPageNumber());
        }
        return organizationIdentityOutputPort.findByName(organizationIdentity.getName(),organizationIdentity.getActivationStatus()
                ,organizationIdentity.getPageSize(),organizationIdentity.getPageNumber());
    }

    @Override
    public OrganizationIdentity viewOrganizationDetails(String organizationId, String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        log.info("Viewing organization detail for user with role {}", userIdentity.getRole());
        if(userIdentity.getRole().equals(ORGANIZATION_ADMIN)){
            OrganizationEmployeeIdentity organizationEmployeeIdentity =
                    organizationEmployeeIdentityOutputPort.findByCreatedBy(userIdentity.getId());
            organizationId = organizationEmployeeIdentity.getOrganization();
        }
            MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
            OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
            List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.getServiceOfferings(organizationIdentity.getId());
            organizationIdentity.setServiceOfferings(serviceOfferings);
            log.info("Service offering has been gotten during view organization detail {}", serviceOfferings);
            OrganizationLoanDetail organizationLoanDetail =
                    organizationLoanDetailOutputPort.findByOrganizationId(organizationIdentity.getId());
            organizationIdentityMapper.mapOrganizationLoanDetailsToOrganization(organizationIdentity,organizationLoanDetail);
            getLoanPercentage(organizationIdentity, organizationLoanDetail);
            int pendingLoanOffer = loanOfferOutputPort.countNumberOfPendingLoanOfferForOrganization(organizationIdentity.getId());
            log.info("Number of pending loan offer in organization with id {} -------> is {}",organizationId, pendingLoanOffer);
            organizationIdentity.setPendingLoanOfferCount(pendingLoanOffer);
        return organizationIdentity;
    }

    private static void getLoanPercentage(OrganizationIdentity organizationIdentity, OrganizationLoanDetail organizationLoanDetail) {
        BigDecimal totalAmountReceived = organizationIdentity.getTotalAmountReceived();
        if (totalAmountReceived != null && totalAmountReceived.compareTo(BigDecimal.ZERO) > 0 &&
                organizationLoanDetail.getOutstandingAmount() != null &&
                organizationLoanDetail.getAmountRepaid() != null) {
            organizationIdentity.setDebtPercentage(
                    organizationLoanDetail.getOutstandingAmount()
                            .divide(totalAmountReceived, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
            );
            organizationIdentity.setRepaymentRate(
                    organizationLoanDetail.getAmountRepaid()
                            .divide(totalAmountReceived, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
            );
        } else {
            organizationIdentity.setDebtPercentage(0.0);
            organizationIdentity.setRepaymentRate(0.0);
        }
    }

    @Override
    public OrganizationIdentity viewTopOrganizationByLoanRequestCount() throws MeedlException {
        Optional<LoanMetrics> loanMetrics = loanMetricsOutputPort.findTopOrganizationWithLoanRequest();
        if (loanMetrics.isEmpty()){
            throw new IdentityException(OrganizationMessages.LOAN_METRICS_NOT_FOUND.getMessage());
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

}
