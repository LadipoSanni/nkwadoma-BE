package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.springframework.data.domain.*;

public interface ViewOrganizationEmployeesUseCase {
    Page<OrganizationEmployeeIdentity> viewOrganizationEmployees(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;
    OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> searchOrganizationAdmin(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException;
}
