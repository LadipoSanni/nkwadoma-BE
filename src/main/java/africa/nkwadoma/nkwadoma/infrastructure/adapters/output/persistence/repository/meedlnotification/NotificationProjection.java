package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlnotification;

public interface NotificationProjection {

    int getUnreadCount();
    int getAllNotificationsCount();
}
