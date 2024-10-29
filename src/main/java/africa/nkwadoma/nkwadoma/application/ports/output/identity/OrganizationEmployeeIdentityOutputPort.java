package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;

import java.util.*;

public interface OrganizationEmployeeIdentityOutputPort {
    OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity);
    OrganizationEmployeeIdentity findById(String id) throws MeedlException;
    OrganizationEmployeeIdentity findByEmployeeId(String employeeId) throws MeedlException;

    Optional<OrganizationEmployeeIdentity> findByCreatedBy(String createdBy) throws MeedlException;

    void delete(String id) throws MeedlException;

    List<OrganizationEmployeeIdentity> findAllByOrganization(String organizationId) throws MeedlException;
}
