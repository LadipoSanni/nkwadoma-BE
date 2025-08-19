package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortType;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cohort {
    private String id;
    private String programId;
    private String organizationId;
    @Size( max = 2500, message = "Cohort description must not exceed 2500 characters" )
    private String cohortDescription;
    private String name;
    private ActivationStatus activationStatus;
    private CohortStatus cohortStatus;
    private CohortType cohortType;
    private LocalDateTime createdAt;
    private BigDecimal tuitionAmount;
    private BigDecimal totalAmountRequested;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal totalAmountReceived;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetail loanDetail;
    private int numberOfLoanees = 0;
    private int numberOfReferredLoanee = 0;
    private double repaymentRate;
    private double debtPercentage;
    private String programName;
    private int stillInTraining ;
    private int numberOfDropout ;
    private int numberEmployed ;
    private int numberOfPendingLoanOffers;
    private int numberOfLoanRequest = 0;
    private int pageSize;
    private int pageNumber;


    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateObjectName(name,"Cohort name cannot be empty","Cohort");
        MeedlValidator.validateUUID(createdBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        MeedlValidator.validateObjectInstance(startDate,"Start date cannot be empty");
        MeedlValidator.validateNegativeAmount(tuitionAmount,"Tuition");
    }

    public void updateValidation() throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateUUID(updatedBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
    }

    public void validateLoanBreakDowns() throws MeedlException {
        MeedlValidator.validateLoanBreakdowns(loanBreakdowns);
    }
}