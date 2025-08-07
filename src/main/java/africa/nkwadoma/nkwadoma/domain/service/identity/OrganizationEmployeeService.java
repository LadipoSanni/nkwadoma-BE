package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
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

import java.util.Collections;

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
    public Page<OrganizationEmployeeIdentity> searchOrganizationAdmin(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateUUID(organizationIdentity.getActorId(), UserMessages.INVALID_USER_ID.getMessage());
        OrganizationEmployeeIdentity organizationEmployeeIdentity
                = organizationEmployeeOutputPort.findByCreatedBy(organizationIdentity.getActorId());
        organizationIdentity.setId(organizationEmployeeIdentity.getOrganization());

        return organizationEmployeeOutputPort.findEmployeesByNameAndRole(
                organizationIdentity, organizationEmployeeIdentity.getMeedlUser().getRole());
    }

    @Override
    public OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getId(), "Valid organization employee id is required");
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
        return organizationEmployeeOutputPort.findAllAdminInOrganization(organizationIdentity.getId(),
                organizationEmployeeIdentity);
    }

    private static void setRolesToView(OrganizationEmployeeIdentity organizationEmployeeIdentity, UserIdentity foundActor) {
        if (organizationEmployeeIdentity.getIdentityRoles() == null ||
                MeedlValidator.isEmpty(organizationEmployeeIdentity.getIdentityRoles())) {
            if (isMeedlStaff(foundActor.getRole())){
                organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getMeedlRoles());
            }
            if (isOrganizationStaff(foundActor.getRole())){
                organizationEmployeeIdentity.setIdentityRoles(IdentityRole.getOrganizationRoles());
            }
        }
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
