package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import org.springframework.data.domain.*;

public interface OrganizationEmployeeIdentityOutputPort {
    OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity);
    OrganizationEmployeeIdentity findById(String id) throws MeedlException;
    OrganizationEmployeeIdentity findByEmployeeId(String employeeId) throws MeedlException;
    Page<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId, int pageNumber, int pageSize) throws MeedlException;
    OrganizationEmployeeIdentity findByCreatedBy(String createdBy) throws MeedlException;
    void delete(String id) throws MeedlException;
}
