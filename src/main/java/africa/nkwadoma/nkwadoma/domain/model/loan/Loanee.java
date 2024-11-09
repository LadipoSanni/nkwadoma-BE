package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class Loanee {

    private String id;
    private String organizationId;
    private String cohortId;
    private String programId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentity user;
    private LoaneeLoanDetail loaneeLoanDetail;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(user);
        MeedlValidator.validateEmail(user.getEmail());
        MeedlValidator.validateDataElement(user.getFirstName());
        MeedlValidator.validateDataElement(user.getLastName());
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validateUUID(cohortId);
        MeedlValidator.validateUUID(createdBy);
        MeedlValidator.validateObjectInstance(loaneeLoanDetail);
    }

}
