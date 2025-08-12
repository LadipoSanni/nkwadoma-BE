package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.enums.*;

public interface OrganizationEmployeeProjection {
    String getId();
    ActivationStatus getActivationStatus();
    String getFirstName();
    String getLastName();
    String getEmail();
}
