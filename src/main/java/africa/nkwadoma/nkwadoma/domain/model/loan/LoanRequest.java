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
    private LocalDateTime createdDate;
    private String reasonForDecliningLoanRequest;
    private LoanRequestStatus status;
    private Loanee loanee;
    private int pageNumber;
    private int pageSize;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getLoaneeLoanDetail());
        MeedlValidator.validateObjectInstance(status);
        MeedlValidator.validateBigDecimalDataElement(loanAmountRequested);
    }

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == BigInteger.ZERO.intValue() ? defaultPageSize : this.pageSize;
    }
}
