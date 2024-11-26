package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
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
    private LoanOffer loanOffer;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity());
        loanee.getUserIdentity().validate();
        MeedlValidator.validateDataElement(loanAccountId);
        MeedlValidator.validateObjectInstance(startDate);
    }

}
