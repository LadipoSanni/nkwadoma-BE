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

    @Override
    public Page<OrganizationEmployeeIdentity> viewOrganizationEmployees
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity);
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getOrganization());
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
    public List<OrganizationEmployeeIdentity> searchOrganizationAdmin(String userId, String name) throws MeedlException {
        MeedlValidator.validateUUID(userId);
        OrganizationEmployeeIdentity organizationEmployeeIdentity
                = organizationEmployeeOutputPort.findByCreatedBy(userId);
        List<OrganizationEmployeeIdentity> organizationEmployeeIdentities = organizationEmployeeOutputPort.findEmployeesByNameAndRole(organizationEmployeeIdentity.getOrganization(),
                name, IdentityRole.ORGANIZATION_ADMIN);
        return organizationEmployeeIdentities;
    }

    @Override
    public Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(String userId,int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(userId);
            OrganizationEmployeeIdentity organizationEmployeeIdentity
                    = organizationEmployeeOutputPort.findByCreatedBy(userId);
        return organizationEmployeeOutputPort.findAllAdminInOrganization(organizationEmployeeIdentity.getOrganization(),
                IdentityRole.ORGANIZATION_ADMIN,pageSize,pageNumber);
    }
}
