package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.time.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loanee {
    private String id;
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        userIdentity.validate();
        MeedlValidator.validateUUID(cohortId);
        MeedlValidator.validateUUID(createdBy);
        MeedlValidator.validateObjectInstance(loaneeLoanDetail);
    }

}
