package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.time.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loanee {
    private String id;
    private String cohortId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime creditScoreUpdatedAt;
    private int creditScore;
    private String registryId;
    private String cohortName;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<LoaneeLoanBreakdown> loanBreakdowns;
    private LoaneeStatus loaneeStatus;
    private LocalDateTime referralDateTime;
    private String referredBy;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateObjectInstance(loaneeLoanDetail);
        validateLoaneeUserIdentity();
    }

    public void validateLoaneeUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        MeedlValidator.validateDataElement(userIdentity.getFirstName(), "User first name is required.");
        MeedlValidator.validateDataElement(userIdentity.getLastName(), "User last name is required.");
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validateUUID(userIdentity.getCreatedBy(), "Id of actor performing this action is required.");
    }

}
