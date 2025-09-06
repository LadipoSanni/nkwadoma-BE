package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;

public interface OrganizationEmployeeEntityProjection {
    String getRequestedBy();
    String getId();
    String getOrganization();
    ActivationStatus getActivationStatus();
    UserEntity getMeedlUser();
    String getCreatedBy();

}
