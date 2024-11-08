package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.*;

import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Loanee {
    private String id;
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentity userIdentity;
    private NextOfKin nextOfKin;
}
