package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
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
    private String firstName;
    private String lastName;
    private BigDecimal loanAmountRequested;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private String reasonForDecliningLoanRequest;
    private LoanRequestStatus status;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private String alternateEmail;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private UserIdentity userIdentity;
    private NextOfKin nextOfKin;
    private LocalDate cohortStartDate;
    private String programName;
    private int pageNumber;
    private int pageSize;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getLoaneeLoanDetail());
        MeedlValidator.validateObjectInstance(status);
        MeedlValidator.validateBigDecimalDataElement(loanAmountRequested);
    }


    public LoanRequest(String id, String firstName, String lastName, String alternateContactAddress, String alternateEmail,
                       String alternatePhoneNumber, String organizationName, BigDecimal loanAmountRequested,
                       BigDecimal initialDeposit, LocalDate startDate, String programName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.alternateContactAddress = alternateContactAddress;
        this.alternateEmail = alternateEmail;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.referredBy = organizationName;
        this.loanAmountRequested = loanAmountRequested;
        this.initialDeposit = initialDeposit;
        this.cohortStartDate = startDate;
        this.programName = programName;
    }


    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == BigInteger.ZERO.intValue() ? defaultPageSize : this.pageSize;
    }
}
