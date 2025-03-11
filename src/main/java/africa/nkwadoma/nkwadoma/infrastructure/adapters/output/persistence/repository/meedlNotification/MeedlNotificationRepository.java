package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeedlNotificationRepository extends JpaRepository<MeedlNotificationEntity, String> {

    List<MeedlNotificationEntity> findAllByUser_Id(String userId, Sort sort);

    @Query(value = "SELECT COUNT(*) FROM meedl_notification_entity WHERE meedl_user = :userId AND is_read = false", nativeQuery = true)
    int countByUserIdAndReadIsFalse(String userId);
}
