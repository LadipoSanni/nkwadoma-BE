package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeedlNotificationRepository extends JpaRepository<MeedlNotificationEntity, String> {

}
