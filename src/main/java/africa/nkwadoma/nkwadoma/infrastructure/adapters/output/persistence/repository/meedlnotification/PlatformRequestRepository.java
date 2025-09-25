package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlnotification;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlnotification.PlatformRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRequestRepository extends JpaRepository<PlatformRequestEntity, String> {
}
