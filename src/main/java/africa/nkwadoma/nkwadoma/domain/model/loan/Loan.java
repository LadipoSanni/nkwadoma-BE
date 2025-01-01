package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.*;
import java.time.*;

@Slf4j
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Loanee loanee;
    private String firstName;
    private String lastName;
    private String loaneeId;
    private String loanOfferId;
    private String loanAccountId;
    private LocalDateTime startDate;
    private LocalDate cohortStartDate;
    private LocalDateTime offerDate;
    private LocalDateTime lastUpdatedDate;
    private LoanOffer loanOffer;
    private LoanStatus loanStatus;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private String cohortName;
    private String programName;
    private String organizationId;
    private int pageNumber;
    private int pageSize;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity(), UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        loanee.getUserIdentity().validate();
        MeedlValidator.validateUUID(loanAccountId, "Please provide a valid loan account identification.");
        MeedlValidator.validateObjectInstance(startDate, LoanMessages.LOAN_START_DATE_MUST_NOT_BE_EMPTY.getMessage());
    }

}
