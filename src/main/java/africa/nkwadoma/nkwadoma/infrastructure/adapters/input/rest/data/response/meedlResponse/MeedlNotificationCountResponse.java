package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MeedlNotificationCountResponse {

    private int unreadCount;
    private int allNotificationsCount;

}
