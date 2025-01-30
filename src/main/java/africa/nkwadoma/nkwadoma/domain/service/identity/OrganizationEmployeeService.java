package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrganizationEmployeeService implements ViewOrganizationEmployeesUseCase {
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;

    @Override
    public Page<OrganizationEmployeeIdentity> viewOrganizationEmployees
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity);
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
    public List<OrganizationEmployeeIdentity>  searchOrganizationAdmin(String userId, String name) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        OrganizationEmployeeIdentity organizationEmployeeIdentity
                = organizationEmployeeOutputPort.findByCreatedBy(userId);
        return organizationEmployeeOutputPort.findEmployeesByNameAndRole(organizationEmployeeIdentity.getOrganization(),
                name, organizationEmployeeIdentity.getMeedlUser().getRole());
    }

    @Override
    public OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity);
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getId());
        return organizationEmployeeOutputPort.findByEmployeeId(organizationEmployeeIdentity.getId());
    }

    @Override
    public Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(String userId,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
            OrganizationEmployeeIdentity organizationEmployeeIdentity
                    = organizationEmployeeOutputPort.findByCreatedBy(userId);

        return organizationEmployeeOutputPort.findAllAdminInOrganization(organizationEmployeeIdentity.getOrganization(),
                organizationEmployeeIdentity.getMeedlUser().getRole(),pageSize,pageNumber);
    }

    @Override
    public Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectName(name,"Name cannot be empty");
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(organizationId);
        if (ObjectUtils.isEmpty(organizationIdentity)) {
            throw new IdentityException(IdentityMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        return organizationEmployeeOutputPort.findAllEmployeesInOrganization(organizationId,name,pageSize,pageNumber);
    }
}
