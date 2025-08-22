package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrganizationEmployeeService implements ViewOrganizationEmployeesUseCase {
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;

    @Override
    public Page<OrganizationEmployeeIdentity> viewOrganizationEmployees
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getOrganization(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageNumber(organizationEmployeeIdentity.getPageNumber());
        MeedlValidator.validatePageSize(organizationEmployeeIdentity.getPageSize());
        Page<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeOutputPort.
                findAllOrganizationEmployees(
                organizationEmployeeIdentity.getOrganization(),
                organizationEmployeeIdentity.getPageNumber(),
                organizationEmployeeIdentity.getPageSize()
        );
        if (ObjectUtils.isEmpty(organizationEmployees)) {
            throw new IdentityException(IdentityMessages.ORGANIZATION_EMPLOYEE_NOT_FOUND.getMessage());
        }
        return organizationEmployees;
    }
    @Override
    public OrganizationEmployeeIdentity viewEmployeeDetail
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_EMPLOYEE_ID.getMessage());

        return organizationEmployeeOutputPort.
                findById(organizationEmployeeIdentity.getId());
    }

    @Override
    public OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_EMPLOYEE_ID.getMessage());
        return organizationEmployeeOutputPort.findByEmployeeId(organizationEmployeeIdentity.getId());
    }
    @Override
    public Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, "Provide an organization employee data.");
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity.getMeedlUser(), "Provide a user entity.");
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getMeedlUser().getId(), UserMessages.INVALID_USER_ID.getMessage());

        UserIdentity foundActor = userIdentityOutputPort.findById(organizationEmployeeIdentity.getMeedlUser().getId());

        OrganizationIdentity organizationIdentity
                = organizationIdentityOutputPort.findByUserId(organizationEmployeeIdentity.getMeedlUser().getId())
                    .orElseThrow(()-> new MeedlException("User does not exist in an organization"));
        setRolesToView(organizationEmployeeIdentity, foundActor);
        setActivationStatuses(organizationEmployeeIdentity);
        boolean actorHasViewPermission = isActorHavingViewPermission(organizationEmployeeIdentity, foundActor);
        if (!actorHasViewPermission){
            log.warn("Actor viewing empty employee list because permission denied");
                return Page.empty(PageRequest.of(
                        organizationEmployeeIdentity.getPageNumber(),
                        organizationEmployeeIdentity.getPageSize()
                ));
        }
        log.info("View employees in organization. before out put port call.");
        return organizationEmployeeOutputPort.searchOrFindAllAdminInOrganization(organizationIdentity.getId(),
                organizationEmployeeIdentity);
    }

    private void setActivationStatuses(OrganizationEmployeeIdentity organizationEmployeeIdentity) {
        if (MeedlValidator.isEmptyCollection(organizationEmployeeIdentity.getActivationStatuses())){
            organizationEmployeeIdentity.setActivationStatuses(ActivationStatus.getActiveLikeStatuses());
        }
    }

    private boolean isActorHavingViewPermission(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        boolean userHavePermission = validateUserPermissionOnEmployeeRolesToView(organizationEmployeeIdentity, foundActor);
        if (!userHavePermission){
            return userHavePermission;
        }
        if (ActivationStatus.PENDING_APPROVAL.equals(organizationEmployeeIdentity.getActivationStatus())){
            return validateUserPermissionToViewPendingInvitesToApprove(organizationEmployeeIdentity.getIdentityRoles(), foundActor);
        }
        return userHavePermission;
    }

    public void setRolesToView(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        if (isPendingApprovalWithoutRoles(organizationEmployeeIdentity)) {
            log.info("View employees pending approval. The roles to view are not given. The actor role is {}", foundActor.getRole());
            assignRolesForPendingApproval(organizationEmployeeIdentity, foundActor);
        }

        if (MeedlValidator.isEmptyCollection(organizationEmployeeIdentity.getIdentityRoles())) {
            log.info("No roles were provided for the organization employee... user role is {}", foundActor.getRole());
            assignDefaultRoles(organizationEmployeeIdentity, foundActor);
        }
    }


    private boolean isPendingApprovalWithoutRoles(OrganizationEmployeeIdentity orgEmployee) {
        return MeedlValidator.isEmptyCollection(orgEmployee.getIdentityRoles()) &&
                ActivationStatus.PENDING_APPROVAL.equals(orgEmployee.getActivationStatus());
    }

    private void assignRolesForPendingApproval(OrganizationEmployeeIdentity orgEmployee, UserIdentity foundActor) {
        if (IdentityRole.isMeedlAdminOrMeedlSuperAdmin(foundActor.getRole())) {
            log.info("The found actor to view pending approval is a meedl staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(IdentityRole.getMeedlRoles());
        }
        else if (IdentityRole.PORTFOLIO_MANAGER.equals(foundActor.getRole())) {
            log.info("The found actor to view employees pending approval is a meedl staff {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(Set.of(IdentityRole.PORTFOLIO_MANAGER, IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE));
        }
        else if (IdentityRole.isOrganizationAdminOrSuperAdmin(foundActor.getRole())) {
            log.info("The found actor viewing employees with pending approval is an organization staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(IdentityRole.getOrganizationRoles());
        }else if(IdentityRole.isCooperateSuperAdmin(foundActor.getRole())) {
            log.info("The found actor viewing employees with pending approval is an cooperation staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(Set.of(IdentityRole.COOPERATE_FINANCIER_ADMIN));
        }
    }

    private void assignDefaultRoles(OrganizationEmployeeIdentity orgEmployee, UserIdentity foundActor) {
        if (IdentityRole.isMeedlStaff(foundActor.getRole())) {
            log.info("The found actor is a meedl staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(IdentityRole.getMeedlRoles());
        }
        if (IdentityRole.isOrganizationStaff(foundActor.getRole())) {
            log.info("The found actor is an organization staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(IdentityRole.getOrganizationRoles());
        }if (IdentityRole.isCooperateSuperAdmin(foundActor.getRole())) {
            log.info("The found actor is an cooperate staff with role {}", foundActor.getRole());
            orgEmployee.setIdentityRoles(IdentityRole.getCooperateFinancierRoles());
        }
    }

    /* ------------------- Role Check Helpers ------------------- */


    private boolean validateUserPermissionToViewPendingInvitesToApprove(Set<IdentityRole> employeeRoles, UserIdentity foundActor) {
        IdentityRole actorRole = foundActor.getRole();
        log.error("Actor role while verifying view permission {}", actorRole);
        switch (actorRole) {
            case ORGANIZATION_ASSOCIATE, PORTFOLIO_MANAGER_ASSOCIATE , COOPERATE_FINANCIER_ADMIN -> {
                log.error("You are not permitted to view pending invites.");
                return Boolean.FALSE;
            }
            case PORTFOLIO_MANAGER -> {
                boolean allowed = employeeRoles.stream().allMatch(
                        role -> role == IdentityRole.PORTFOLIO_MANAGER || role == IdentityRole.PORTFOLIO_MANAGER_ASSOCIATE
                );
                if (!allowed) {
                    log.error("Portfolio Managers can only view Portfolio Managers or Meedl Associates.");
                    return Boolean.FALSE;
                }
            }

            case MEEDL_SUPER_ADMIN, MEEDL_ADMIN -> {
                boolean allowed = employeeRoles.stream().allMatch(IdentityRole::isMeedlStaff);
                if (!allowed) {
                    log.error("You are only permitted to view Meedl staff invites.");
                    return Boolean.FALSE;
                }
            }

            case ORGANIZATION_ADMIN, ORGANIZATION_SUPER_ADMIN -> {
                boolean allowed = employeeRoles.stream().allMatch(IdentityRole::isOrganizationStaff);
                if (!allowed) {
                    log.error("You are only permitted to view Organization staff invites.");
                    return Boolean.FALSE;
                }
            }
            case COOPERATE_FINANCIER_SUPER_ADMIN -> {
                boolean allowed = employeeRoles.stream().allMatch(IdentityRole::isCooperateSuperAdmin);
                if (!allowed) {
                    log.error("You are only permited to view Cooperate financier staff invites.");
                    return Boolean.FALSE;
                }
            }
            default -> {
                log.error("You are not permitted to view pending invites user with role {}", actorRole);
                return Boolean.FALSE;
            }
        }
        log.info("Actor is permitted to view staffs. Role {}", actorRole);
        return Boolean.TRUE;
    }


    public boolean validateUserPermissionOnEmployeeRolesToView(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        if (MeedlValidator.isEmptyCollection(organizationEmployeeIdentity.getIdentityRoles())){
            log.info("There are no employee roles therefore the roles will be set to the default of the staff");
        }
        boolean userIsMeedlStaff = IdentityRole.isMeedlStaff(foundActor.getRole());
        boolean employeeHasMeedlRole = organizationEmployeeIdentity.getIdentityRoles().stream().anyMatch(IdentityRole::isMeedlStaff);
        boolean employeeHasOrganizationRole = organizationEmployeeIdentity.getIdentityRoles().stream().anyMatch(IdentityRole::isOrganizationStaff);
        boolean userIsCorporateStaff = IdentityRole.isCooperateFinancier(foundActor.getRole());
        boolean employeeIsCorporateStaff = organizationEmployeeIdentity.getIdentityRoles().stream().anyMatch(IdentityRole::isCooperateFinancier);

        if (!userIsMeedlStaff && employeeHasMeedlRole) {
            log.error("A none meedl staff {} is attempting to view staffs that are meedl staffs. \n ---------------------------------------------------------------------> Roles atempted to view {}", foundActor, organizationEmployeeIdentity.getIdentityRoles());
            return Boolean.FALSE;
        }

        if (userIsMeedlStaff && employeeHasOrganizationRole) {
            log.error("A meedl staff {} is attempting to view staffs that are in another organization not meedl. \n ---------------------------------------------------------------------> Roles atempted to view {}", foundActor, organizationEmployeeIdentity.getIdentityRoles());
            return Boolean.FALSE;
        }
        if (employeeIsCorporateStaff && !userIsCorporateStaff) {
            log.error("Unauthorized: non-corporate staff {} attempted to view corporate staff. \n ---> Roles attempted: {}",
                    foundActor, organizationEmployeeIdentity.getIdentityRoles());
            return Boolean.FALSE;
        }

        log.info("Actor has the permissions to view");
        return Boolean.TRUE;
    }

    @Override
    public Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        if (ObjectUtils.isEmpty(organizationIdentity)) {
            throw new ResourceNotFoundException(IdentityMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        return organizationEmployeeOutputPort.findAllEmployeesInOrganization(organizationId,name,pageSize,pageNumber);
    }

    @Override
    public String respondToColleagueInvitation(String actorId,String organizationEmployeeId,ActivationStatus activationStatus) throws MeedlException {
        MeedlValidator.validateUUID(organizationEmployeeId,IdentityMessages.INVALID_ORGANIZATION_EMPLOYEE.getMessage());
        MeedlValidator.validateObjectInstance(activationStatus,"Activation status cannot be null");
        decisionMustEitherBeApprovedOrDeclined(activationStatus);
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationEmployeeOutputPort.findById(organizationEmployeeId);
        if (organizationEmployeeIdentity.getActivationStatus().equals(ActivationStatus.ACTIVE)) {
            throw new IdentityException(OrganizationMessages.ORGANIZATION_EMPLOYEE_IS_ACTIVE.getMessage());
        }

        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationEmployeeIdentity.getOrganization());

        if (activationStatus.equals(ActivationStatus.APPROVED)) {
            asynchronousMailingOutputPort.sendColleagueEmail(organizationIdentity.getName(), organizationEmployeeIdentity.getMeedlUser());
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.INVITED);
            organizationEmployeeOutputPort.save(organizationEmployeeIdentity);
            return "Colleague invitation APPROVED for " + organizationEmployeeIdentity.getMeedlUser().getFirstName() + " "
                    + organizationEmployeeIdentity.getMeedlUser().getLastName();
        }else {
            organizationEmployeeIdentity.setActivationStatus(ActivationStatus.DECLINED);
            organizationEmployeeOutputPort.save(organizationEmployeeIdentity);
            UserIdentity createdBy = userIdentityOutputPort.findById(organizationEmployeeIdentity.getCreatedBy());
            asynchronousNotificationOutputPort.sendDeclineColleagueNotification(organizationEmployeeIdentity,userIdentity,createdBy);
            return "Colleague invitation DECLINED for " + organizationEmployeeIdentity.getMeedlUser().getFirstName() +" "
                    + organizationEmployeeIdentity.getMeedlUser().getLastName();
        }
    }

    private void decisionMustEitherBeApprovedOrDeclined(ActivationStatus activationStatus) throws IdentityException {
        if (! activationStatus.equals(ActivationStatus.APPROVED) && !activationStatus.equals(ActivationStatus.DECLINED)) {
            throw new IdentityException(OrganizationMessages.DECISION_CAN_EITHER_BE_APPROVED_OR_DECLINED.getMessage());
        }
    }
}
