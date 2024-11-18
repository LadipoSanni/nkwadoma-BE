package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    private String id;
    private String referredBy;
    private BigDecimal loanAmountRequested;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private String reasonForDecliningLoanRequest;
    private LoanRequestStatus status;
    private Loanee loanee;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getLoaneeLoanDetail());
        MeedlValidator.validateObjectInstance(dateTimeApproved);
        MeedlValidator.validateBigDecimalDataElement(loanAmountRequested);
    }
}
