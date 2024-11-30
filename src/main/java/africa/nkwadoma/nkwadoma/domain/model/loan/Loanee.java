package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.time.LocalDateTime;

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
    private LoaneeStatus loaneeStatus;
    private LocalDateTime referralDateTime;
    private String referredBy;
    private String fullName;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(cohortId);
        MeedlValidator.validateUUID(createdBy);
        MeedlValidator.validateObjectInstance(loaneeLoanDetail);
        validateLoaneeUserIdentity();
    }

    public void validateLoaneeUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateDataElement(userIdentity.getFirstName());
        MeedlValidator.validateDataElement(userIdentity.getLastName());
        MeedlValidator.validateDataElement(userIdentity.getEmail());
        MeedlValidator.validateUUID(createdBy);
    }

}
