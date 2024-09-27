package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;

public interface OrganizationEmployeeIdentityOutputPort {
    OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity);
}
