package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;

import java.time.LocalDateTime;

public interface OrganizationEmployeeProjection {
    String getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    ActivationStatus getActivationStatus();
    String getUserId();
    LocalDateTime getCreatedAt();
    IdentityRole getRole();
}
