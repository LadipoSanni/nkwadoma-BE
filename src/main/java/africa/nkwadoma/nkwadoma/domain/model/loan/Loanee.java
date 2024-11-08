package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import lombok.*;

import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loanee {
    private String id;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String alternateEmail;
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentity userIdentity;
}
