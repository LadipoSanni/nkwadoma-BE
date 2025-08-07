package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class LoaneeLoanAggregate {

    private String id;
    private String loaneeId;
    private BigDecimal historicalDebt;
    private BigDecimal totalAmountOutstanding;
    private int numberOfLoans;
    private String firstName;
    private String lastName;
    private String email;
    private Loanee loanee;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee,"Loanee cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(totalAmountOutstanding,"Total amount outstanding cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(historicalDebt,"Historical debt cannot be empty");
    }
}
