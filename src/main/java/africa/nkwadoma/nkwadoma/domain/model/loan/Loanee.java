package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
public class
Loanee {
    private String id;
    private String organizationId;
    private String cohortId;
    private String programId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentity loanee;
    private LoaneeLoanDetail loaneeLoanDetail;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateEmail(loanee.getEmail());
        MeedlValidator.validateDataElement(loanee.getFirstName());
        MeedlValidator.validateDataElement(loanee.getLastName());
        MeedlValidator.validateUUID(cohortId);
        MeedlValidator.validateUUID(createdBy);
        MeedlValidator.validateObjectInstance(loaneeLoanDetail);
    }

}
