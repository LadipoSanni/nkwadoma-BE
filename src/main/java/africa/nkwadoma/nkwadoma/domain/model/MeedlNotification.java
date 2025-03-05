package africa.nkwadoma.nkwadoma.domain.model;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Builder
public class MeedlNotification {

    private String id;
    private String contentId;
    private String title;
    private String name;
    private UserIdentity user;
    private boolean isRead;
    private LocalDateTime timestamp;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(contentId,"Content id cannot be empty");
        MeedlValidator.validateObjectInstance(title,"Title cannot be empty");
        MeedlValidator.validateObjectInstance(user,"User identity cannot be empty");
        MeedlValidator.validateObjectInstance(timestamp,"Timestamp cannot be empty");
    }
}
