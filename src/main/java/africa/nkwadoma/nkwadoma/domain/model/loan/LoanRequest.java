package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
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
    private String organizationId;
    private String programId;
    private String name;
    private String referredBy;
    private LoanDecision loanRequestDecision;
    private String declineReason;
    private BigDecimal loanAmountRequested;
    private BigDecimal loanAmountApproved;
    private LoanReferralStatus loanReferralStatus;
    private LocalDateTime dateTimeApproved;
    private LocalDateTime dateTimeOffered;
    private LocalDateTime createdDate;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private LoanProduct loanProduct;
    private LoanRequestStatus status;
    private Loanee loanee;
    private CohortLoanee cohortLoanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<LoanBreakdown> loanBreakdowns;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
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
    private String loaneeId;
    private String loanOfferId;
    private int creditScore;
    private String actorId;
    private String cohortLoaneeId;
    private String cohortId;
    private boolean isVerified;
    private OnboardingMode onboardingMode;


    public void validateLoanProductIdAndAmountApproved() throws MeedlException {
        MeedlValidator.validateUUID(this.id, "Please provide a valid loan request identification");
        MeedlValidator.validateUUID(this.loanProductId, LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());
        MeedlValidator.validateBigDecimalDataElement(this.loanAmountApproved);
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(id, LoaneeMessages.LOAN_REFERRAL_ID_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateBigDecimalDataElement(loanAmountRequested,
                LoanMessages.LOAN_AMOUNT_REQUESTED_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(referredBy,"Referred by cannot be empty");
        MeedlValidator.validateObjectInstance(status,"Status cannot be empty");
        MeedlValidator.validateObjectInstance(createdDate,"Created date cannot be empty");
    }


    public LoanRequest(String id, String firstName, String lastName, String alternateContactAddress, String alternateEmail,
                       String alternatePhoneNumber, String organizationName, BigDecimal loanAmountRequested,
                       BigDecimal initialDeposit, LocalDate startDate, String programName) {
        this.id = id;
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
