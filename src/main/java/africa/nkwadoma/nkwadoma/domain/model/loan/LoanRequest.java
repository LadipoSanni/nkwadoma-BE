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
    private String loanRequestDecision;
    private String declineReason;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private String reasonForDecliningLoanRequest;
    private LoanProduct loanProduct;
    private LoanRequestStatus status;
    private Loanee loanee;

    public static void validate(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validateUUID(loanRequest.getId());
        MeedlValidator.validateUUID(loanRequest.getLoanProductId());
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getLoaneeLoanDetail());
        MeedlValidator.validateObjectInstance(status);
        MeedlValidator.validateObjectInstance(dateTimeApproved);
        MeedlValidator.validateBigDecimalDataElement(loanAmountRequested);
    }
}
