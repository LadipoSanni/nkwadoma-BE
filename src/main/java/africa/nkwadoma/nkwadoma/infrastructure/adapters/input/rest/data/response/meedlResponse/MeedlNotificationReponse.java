package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MeedlNotificationReponse {

    private String id;
    private String contentId;
    private String title;
    private String name;
    private String firstName;
    private boolean isRead;
    private LocalDateTime timestamp;
    private boolean callToAction;
    private String senderMail;
    private String senderFullName;
    private String contentDetail;
    private String duration;

}
