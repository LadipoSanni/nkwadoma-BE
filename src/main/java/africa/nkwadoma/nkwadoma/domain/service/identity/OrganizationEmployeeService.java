package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
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
    public OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getId(), "Valid organization employee id is required");
        return organizationEmployeeOutputPort.findByEmployeeId(organizationEmployeeIdentity.getId());
    }

    @Override
    public Page<OrganizationEmployeeIdentity> searchOrganizationAdmin(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, "Provide an organization employee data.");
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity.getMeedlUser(), "Provide a user entity.");
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getMeedlUser().getId(), UserMessages.INVALID_USER_ID.getMessage());

        UserIdentity foundActor = userIdentityOutputPort.findById(organizationEmployeeIdentity.getMeedlUser().getId());

        OrganizationIdentity organizationIdentity
                = organizationIdentityOutputPort.findByUserId(organizationEmployeeIdentity.getMeedlUser().getId())
                .orElseThrow(()-> new MeedlException("User does not exist in an organization"));
        boolean actorHasViewPermission = isActorHavingViewPermission(organizationEmployeeIdentity, foundActor);
        if (!actorHasViewPermission){
            return Page.empty(PageRequest.of(
                    organizationEmployeeIdentity.getPageNumber(),
                    organizationEmployeeIdentity.getPageSize()
            ));
        }
        setRolesToView(organizationEmployeeIdentity, foundActor);
        return organizationEmployeeOutputPort.searchAdmins(organizationIdentity.getId(),
                organizationEmployeeIdentity);
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
        boolean actorHasViewPermission = isActorHavingViewPermission(organizationEmployeeIdentity, foundActor);
        if (!actorHasViewPermission){
            return Page.empty(PageRequest.of(
                    organizationEmployeeIdentity.getPageNumber(),
                    organizationEmployeeIdentity.getPageSize()
            ));
        }
        setRolesToView(organizationEmployeeIdentity, foundActor);
        return organizationEmployeeOutputPort.findAllAdminInOrganization(organizationIdentity.getId(),
                organizationEmployeeIdentity);
    }

    private boolean isActorHavingViewPermission(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        boolean userHavePermission = validateUserPermissionOnEmployeeRolesToView(organizationEmployeeIdentity.getIdentityRoles(), foundActor);
        if (!userHavePermission){
            return userHavePermission;
        }
        if (ActivationStatus.PENDING_APPROVAL.equals(organizationEmployeeIdentity.getActivationStatus())){
            return validateUserPermissionToViewPendingInvitesToApprove(organizationEmployeeIdentity.getIdentityRoles(), foundActor);
        }
        return userHavePermission;
    }

    private void setRolesToView(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        if (organizationEmployeeIdentity.getIdentityRoles() == null ||
                MeedlValidator.isEmpty(organizationEmployeeIdentity.getIdentityRoles())) {
            log.info("No roles were provided for the organization employee... user role is {}", foundActor.getRole());
            if (IdentityRole.isMeedlStaff(foundActor.getRole())){
                organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getMeedlRoles());
            }
            if (IdentityRole.isOrganizationStaff(foundActor.getRole())){
                organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getOrganizationRoles());
            }
        }
    }
    private boolean validateUserPermissionToViewPendingInvitesToApprove(Set<IdentityRole> employeeRoles, UserIdentity foundActor) {
        IdentityRole actorRole = foundActor.getRole();
        log.error("Actor role while verifying view permission {}", actorRole);
        switch (actorRole) {
            case ORGANIZATION_ASSOCIATE, MEEDL_ASSOCIATE -> {
                log.error("You are not permitted to view pending invites.");
                return Boolean.FALSE;
            }
            case PORTFOLIO_MANAGER -> {
                boolean allowed = employeeRoles.stream().allMatch(
                        role -> role == IdentityRole.PORTFOLIO_MANAGER || role == IdentityRole.MEEDL_ASSOCIATE
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
            default -> {
                log.error("You are not permitted to view pending invites user with role {}", actorRole);
                return Boolean.FALSE;
            }
        }
        log.info("Actor is permitted to view staffs. Role {}", actorRole);
        return Boolean.TRUE;
    }


    public boolean validateUserPermissionOnEmployeeRolesToView(Set<IdentityRole> employeeRoles, UserIdentity foundActor) {
        boolean userIsMeedlStaff = IdentityRole.isMeedlStaff(foundActor.getRole());
        boolean employeeHasMeedlRole = employeeRoles.stream().anyMatch(IdentityRole::isMeedlStaff);
        boolean employeeHasOrganizationRole = employeeRoles.stream().anyMatch(IdentityRole::isOrganizationStaff);

        if (!userIsMeedlStaff && employeeHasMeedlRole) {
            log.error("A none meedl staff {} is attempting to view staffs that are meedl staffs. \n ---------------------------------------------------------------------> Roles atempted to view {}", foundActor, employeeRoles);
            return Boolean.FALSE;
        }

        if (userIsMeedlStaff && employeeHasOrganizationRole) {
            log.error("A meedl staff {} is attempting to view staffs that are in another organization not meedl. \n ---------------------------------------------------------------------> Roles atempted to view {}", foundActor, employeeRoles);
            return Boolean.FALSE;
        }
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
}
