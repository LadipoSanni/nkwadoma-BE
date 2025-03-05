package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse;


import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MeedlReponse {

    private String id;
    private String contentId;
    private String title;
    private String name;
    private String firstName;
    private boolean isRead;
    private LocalDateTime timestamp;

}
