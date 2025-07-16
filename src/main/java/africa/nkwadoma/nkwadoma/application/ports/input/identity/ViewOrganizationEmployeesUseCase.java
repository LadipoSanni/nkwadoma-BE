package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.springframework.data.domain.*;

public interface ViewOrganizationEmployeesUseCase {
    Page<OrganizationEmployeeIdentity> viewOrganizationEmployees(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;
    OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> searchOrganizationAdmin(OrganizationIdentity organizationIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(String userId,int pageSize , int pageNumber) throws MeedlException;

    Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException;
}
