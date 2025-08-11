package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
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

    List<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId);

    Page<OrganizationEmployeeIdentity> searchAdmins(String organizationId, OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;
    Page<OrganizationEmployeeIdentity> findAllAdminInOrganization(String organizationId, OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    @Deprecated
    Page<OrganizationEmployeeIdentity> findAllEmployeesInOrganization(String organizationId,String name, int pageSize, int pageNumber) throws MeedlException;
    Optional<OrganizationEmployeeIdentity> findByMeedlUserId(String meedlUserId) throws MeedlException;

    List<OrganizationEmployeeIdentity> findAllEmployeesInOrganizationByOrganizationIdAndRole(String organizationId, IdentityRole identityRole) throws MeedlException;

    OrganizationEmployeeIdentity findByRoleAndOrganizationId(String organization, IdentityRole identityRole) throws MeedlException;
}
