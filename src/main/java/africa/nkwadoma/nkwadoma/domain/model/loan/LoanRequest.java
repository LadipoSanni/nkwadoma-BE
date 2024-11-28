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
    private String loanProductId;
    private String referredBy;
    private String firstName;
    private String lastName;
    private LoanDecision loanRequestDecision;
    private String declineReason;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private String reasonForDecliningLoanRequest;
    private LoanProduct loanProduct;
    private LoanRequestStatus status;
    private Loanee loanee;
    private LocalDate cohortStartDate;
    private String programName;
    private int pageNumber;
    private int pageSize;


    public static void validate(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validateUUID(loanRequest.getId());
        MeedlValidator.validateUUID(loanRequest.getLoanProductId());
        MeedlValidator.validateBigDecimalDataElement(loanRequest.getLoanAmountApproved());
    }

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
