package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification.MeedlNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeedlNotificationRepository extends JpaRepository<MeedlNotificationEntity, String> {

    Page<MeedlNotificationEntity> findAllByUser_Id(Pageable pageRequest, String userId);

    @Query(value = "SELECT " +
            "COALESCE(SUM(CASE WHEN read = false THEN 1 ELSE 0 END), 0) AS unread_count, " +
            "COUNT(*) AS all_notifications_count " +
            "FROM meedl_notification_entity " +
            "WHERE meedl_user = :userId",
            nativeQuery = true)
    NotificationProjection getNotificationCounts(String userId);

    void deleteAllByUserId(String id);

}
