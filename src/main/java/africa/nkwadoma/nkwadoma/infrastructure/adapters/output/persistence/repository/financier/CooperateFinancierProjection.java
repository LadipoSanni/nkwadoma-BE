package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;

import java.time.LocalDateTime;

public interface CooperateFinancierProjection {

    String getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    IdentityRole getRole();
    ActivationStatus getStatus();
    LocalDateTime getCreatedAt();
    String getInviteeName();
}
