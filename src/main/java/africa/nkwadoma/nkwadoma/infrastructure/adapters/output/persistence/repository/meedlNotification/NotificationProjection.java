package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlNotification;

public interface NotificationProjection {

    int getUnreadCount();
    int getAllNotificationsCount();
}
