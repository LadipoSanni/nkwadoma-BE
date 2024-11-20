package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Loan {
    private String id;
    private Loanee loanee;
    private String loaneeId;
    private String loanOfferId;
    private String loanAccountId;
    private LocalDateTime startDate;
    private LocalDateTime lastUpdatedDate;

//    private LoanOffer loanOffer;
    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity());
//        UserIdentityValidator.validateUserIdentity(loanee.getUserIdentity());
        MeedlValidator.validateDataElement(loanAccountId);
        MeedlValidator.validateObjectInstance(startDate);
    }

}
