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
    private String organizationName;
    private String firstName;
    private String lastName;
    private String referredBy;
    private String cohortId;
    private BigDecimal loanAmountRequested;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private String reasonForDecliningLoanRequest;
    private LoanRequestStatus status;
    private Loanee loanee;
    private LocalDate cohortStartDate;
    private String programName;
    private int pageNumber;
    private int pageSize;


    public LoanRequest(String id, String firstName, String lastName, String organizationName, BigDecimal loanAmountRequested,
                       LocalDateTime createdDate, BigDecimal initialDeposit, LocalDate cohortStartDate, String programName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizationName = organizationName;
        this.loanAmountRequested = loanAmountRequested;
        this.createdDate = createdDate;
        this.initialDeposit = initialDeposit;
        this.cohortStartDate = cohortStartDate;
        this.programName = programName;
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
