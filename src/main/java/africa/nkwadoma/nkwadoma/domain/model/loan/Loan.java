package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Loanee loanee;
    private String loaneeId;
    private String loanOfferId;
    private String loanAccountId;
    private LocalDateTime startDate;
    private LocalDateTime lastUpdatedDate;
    private LoanOffer loanOffer;
    private LoanStatus loanStatus;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity());
        loanee.getUserIdentity().validate();
        MeedlValidator.validateUUID(loanAccountId, "Please provide a valid loan account identification.");
        MeedlValidator.validateObjectInstance(startDate);
    }

}
