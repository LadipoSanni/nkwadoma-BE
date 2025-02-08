package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.*;

import java.util.List;

public interface ViewOrganizationEmployeesUseCase {
    Page<OrganizationEmployeeIdentity> viewOrganizationEmployees(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;
    OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    List<OrganizationEmployeeIdentity> searchOrganizationAdmin(String userId, String name) throws MeedlException;

    Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(String userId,int pageSize , int pageNumber) throws MeedlException;

    Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException;
}
