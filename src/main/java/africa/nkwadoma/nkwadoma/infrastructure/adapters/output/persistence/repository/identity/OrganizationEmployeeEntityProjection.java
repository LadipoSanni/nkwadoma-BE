package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

public interface OrganizationEmployeeEntityProjection {
    String getRequestedBy();
    String getId();
    String getOrganization();
    ActivationStatus getActivationStatus();
    UserEntity getMeedlUser();
    String getCreatedBy();

}
