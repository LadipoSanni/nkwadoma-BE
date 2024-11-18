package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Loan {
    private String id;
    private Loanee loanee;
    private String loanAccountId;
    private LocalDateTime startDate;
    private LocalDateTime lastUpdatedDate;

//    private LoanOffer loanOffer;

    public void setStartDate(LocalDateTime startDate) throws MeedlException {
        if (startDate.isAfter(LocalDateTime.now())) {
            throw new MeedlException("Start date cannot be in the future.");
        }
        this.startDate = startDate;
    }
}
