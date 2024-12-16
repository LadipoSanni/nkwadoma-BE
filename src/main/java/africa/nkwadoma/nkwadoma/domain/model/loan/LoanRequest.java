package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

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
    private String cohortId;
    private String loanReferralId;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private String reasonForDecliningLoanRequest;
    private LoanProduct loanProduct;
    private LoanRequestStatus status;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<LoanBreakdown> loanBreakdowns;
    private String alternateEmail;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private UserIdentity userIdentity;
    private NextOfKin nextOfKin;
    private LocalDate cohortStartDate;
    private String programName;
    private String cohortName;
    private int pageNumber;
    private int pageSize;


    public static void validate(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validateUUID(loanRequest.getId(), "Please provide a valid loan product identification");
        MeedlValidator.validateUUID(loanRequest.getLoanProductId(), LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        MeedlValidator.validateBigDecimalDataElement(loanRequest.getLoanAmountApproved());
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateBigDecimalDataElement(loanee.getLoaneeLoanDetail().getAmountRequested(), LoanMessages.LOAN_AMOUNT_REQUESTED_MUST_NOT_BE_EMPTY.getMessage());
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
