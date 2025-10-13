package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.*;
import java.time.*;
import java.util.*;

@Slf4j
@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Loanee loanee;
    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String loaneeId;
    private String cohortId;
    private String loanOfferId;
    private String loanAccountId;
    private LoanRequestStatus status;
    private LocalDateTime startDate;
    private LocalDate cohortStartDate;
    private BigDecimal loanAmountRequested;
    private LocalDateTime offerDate;
    private LocalDateTime lastUpdatedDate;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
    private LoanOffer loanOffer;
    private LoanStatus loanStatus;
    private BigDecimal initialDeposit;
    private BigDecimal loanAmountApproved;
    private BigDecimal tuitionAmount;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private String cohortName;
    private String programName;
    private String programId;
    private String referredBy;
    private String organizationId;
    private String actorId;
    private String cohortLoaneeId;
    private BigDecimal loanAmountOutstanding;
    private BigDecimal loanAmountRepaid;
    private double interestRate;
    private int pageNumber;
    private int pageSize;
    private String organizationName;
    private LoanDetailSummary loanDetailSummary;
    private BigDecimal repaymentRate;
    private BigDecimal debtPercentage;
    private BigDecimal interestIncurred;


    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(loanAccountId, "Please provide a valid loan account identification.");
        MeedlValidator.validateUUID(loanOfferId, "Please provide a valid loan offer identification.");
        MeedlValidator.validateObjectInstance(loanStatus, "Loan status cannot be empty.");
        MeedlValidator.validateObjectInstance(startDate, LoanMessages.LOAN_START_DATE_MUST_NOT_BE_EMPTY.getMessage());
    }

    public Loan buildLoan(Loanee foundLoanee, String loanAccountId, String loanOfferId, LocalDateTime startDate) {
        log.info("Loan start date {} while building loan with start date provided for loanee with id {} ", startDate,foundLoanee.getId() );
        return Loan.builder().loanee(foundLoanee).loanAccountId(loanAccountId).loanOfferId(loanOfferId).
                startDate(startDate).loanStatus(LoanStatus.PERFORMING).build();
    }
    public Loan buildLoan(Loanee foundLoanee, String loanAccountId, String loanOfferId, LoanOffer loanOffer) {
        LocalDateTime startDate = LocalDateTime.now();

        if (!ObjectUtils.isEmpty(loanOffer.getStartDate())){
            log.warn("Cohort start date is provided {}", loanOffer.getStartDate());
            startDate = loanOffer.getStartDate().atStartOfDay();
        }
        return Loan.builder().loanee(foundLoanee).loanAccountId(loanAccountId).loanOfferId(loanOfferId).
                startDate(startDate).loanStatus(LoanStatus.PERFORMING).build();
    }
}
