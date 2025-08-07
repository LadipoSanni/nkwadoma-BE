package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import org.springframework.data.domain.*;

import java.util.*;

public interface OrganizationEmployeeIdentityOutputPort {
    OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity);

    OrganizationEmployeeIdentity findById(String id) throws MeedlException;

    OrganizationEmployeeIdentity findByEmployeeId(String employeeId) throws MeedlException;

    Page<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId, int pageNumber, int pageSize) throws MeedlException;

    OrganizationEmployeeIdentity findByCreatedBy(String createdBy) throws MeedlException;

    void delete(String id) throws MeedlException;

    void deleteEmployee(String id) throws IdentityException;

    List<OrganizationEmployeeIdentity> findAllByOrganization(String organizationId) throws MeedlException;

    Page<OrganizationEmployeeIdentity> findEmployeesByNameAndRole(OrganizationIdentity organizationIdentity, IdentityRole identityRole) throws MeedlException;

    List<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId);
    Page<OrganizationEmployeeIdentity> findAllAdminInOrganization(String organizationId, IdentityRole identityRole,int pageSize, int pageNumber) throws MeedlException;

    Page<OrganizationEmployeeIdentity> findAllEmployeesInOrganization(String organizationId,String name, int pageSize, int pageNumber) throws MeedlException;
    Optional<OrganizationEmployeeIdentity> findByMeedlUserId(String meedlUserId) throws MeedlException;

    List<OrganizationEmployeeIdentity> findAllEmployeesInOrganizationByOrganizationIdAndRole(String organizationId, IdentityRole identityRole) throws MeedlException;

    OrganizationEmployeeIdentity findByRoleAndOrganizationId(String organization, IdentityRole identityRole) throws MeedlException;
}
